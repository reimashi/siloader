package com.github.reimashi.siloader;

import com.github.reimashi.siloader.data.*
import com.github.reimashi.siloader.lang.SpatialPoint
import com.github.reimashi.siloader.services.DatabaseService
import org.kohsuke.args4j.CmdLineException
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import ucar.ma2.ArrayDouble
import ucar.ma2.ArrayFloat
import ucar.ma2.ArrayInt
import ucar.ma2.ArrayShort
import ucar.nc2.NetcdfFile
import ucar.nc2.Variable
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import java.util.logging.Logger

class SILoaderMain {
    val log = Logger.getLogger("Main")
    var startTime: Date? = null

    var warehouseDatabaseSrv: DatabaseService? = null
    var tmpDatabaseSrv: DatabaseService? = null

    @Option(name = "-h", usage = "Muestra la ayuda")
    private var showHelp: Boolean = false

    @Option(name = "-s12", usage = "Los datos comenzarán las 12:00 en lugar de a las 00:00")
    private var skipTwelve: Boolean = false

    @Option(name = "-d", usage = "Fecha en formato dd/mm/aaaa de los datos a insertar")
    private var date: String? = LocalDateTime.now().dayOfMonth.toString() + "/" + LocalDateTime.now().monthValue.toString() + "/" + LocalDateTime.now().year.toString();

    @Option(name = "-wrf", usage = "Ruta del dataset WRF, en formato NetCDF")
    private var wrfPath: String? = "wrf.nc4"

    @Option(name = "-ww3", usage = "Ruta del dataset WW3, en formato NetCDF")
    private var ww3Path: String? = "ww3.nc"

    @Option(name = "-marine", usage = "Ruta del dataset Marine, en formato NetCDF")
    private var marinePath: String? = "marine.nc"

    @Option(name = "-dbUrl", usage = "URL de la base de datos MySQL")
    private var dbUrl: String? = "localhost"

    @Option(name = "-dbUser", usage = "Usuario de la base de datos MySQL")
    private var dbUser: String? = "esei"

    @Option(name = "-dbPass", usage = "Contraseña de la base de datos MySQL")
    private var dbPass: String? = "eseipass"

    @Option(name = "-dbTmpName", usage = "Nombre de la base de datos intermedia")
    private var dbTmpName: String? = "work_si_tmp"

    @Option(name = "-dbWarehouseName", usage = "Nombre de la base de datos del datawarehouse")
    private var dbWarehouseName: String? = "work_si"

    fun main(args: Array<String>) {
        val parser = CmdLineParser(this)

        try {
            parser.parseArgument(HashSet(args.asList()))

            try {
                val dateParser = SimpleDateFormat("dd/MM/yyyy")
                this.startTime = dateParser.parse(this.date)

                if (this.skipTwelve) {
                    val cal = Calendar.getInstance()
                    cal.time = this.startTime
                    cal.add(Calendar.HOUR, 12)
                    this.startTime = cal.time
                }
            } catch(e: NullPointerException) {
                log.severe("No se ha podido parsear el parametro de fecha. " + e.message)
            }

            if (this.showHelp) {
                parser.printUsage(System.out);
            }
            else if (this.startTime == null) {
                log.severe("No se ha podido determinar la fecha de los dataset. Se debe especificar una fecha valida mediante el parametro -d.");
            }
            else if (this.dbUrl != null && this.dbUser != null && this.dbPass != null && this.dbTmpName != null && this.dbWarehouseName != null) {
                warehouseDatabaseSrv = DatabaseService(dbUrl = this.dbUrl!!, dbUser = this.dbUser!!, dbPass = this.dbPass!!, dbTable = this.dbWarehouseName!!)
                tmpDatabaseSrv = DatabaseService(dbUrl = this.dbUrl!!, dbUser = this.dbUser!!, dbPass = this.dbPass!!, dbTable = this.dbTmpName!!)

                var ww3Ncfile: NetcdfFile? = null
                var wrfNcfile: NetcdfFile? = null
                var marineNcfile: NetcdfFile? = null

                warehouseDatabaseSrv!!.start()
                tmpDatabaseSrv!!.start()

                try {
                    ww3Ncfile = NetcdfFile.openInMemory(ww3Path)
                    wrfNcfile = NetcdfFile.openInMemory(wrfPath)
                    marineNcfile = NetcdfFile.openInMemory(marinePath)

                    log.info("Limpiando base de datos temporal...")
                    tmpDatabaseSrv!!.truncate("wrf");
                    tmpDatabaseSrv!!.truncate("ww3");
                    tmpDatabaseSrv!!.truncate("marine");
                    log.info("Base de datos temporal limpiada con éxito!")

                    log.info("Cargando archivos a base de datos temporal...")
                    loadWw3File(ww3Ncfile);
                    loadWrfFile(wrfNcfile);
                    loadMarineFile(marineNcfile);
                    log.info("Archivos cargados a base de datos temporal con éxito!")

                    log.info("Cargando archivos a base de datos temporal...")
                    loadData();
                    log.info("Datos cargados con éxito!")
                } catch (ioe: IOException) {
                    log.severe("Error al abrir un fichero de entrada. " + ioe.message)
                } finally {
                    try {
                        ww3Ncfile?.close()
                        wrfNcfile?.close()
                        marineNcfile?.close()
                    } catch (ioe: IOException) {
                        log.severe("Error al cerrar los ficheros de entrada. " + ioe.message)
                    }
                }

                warehouseDatabaseSrv!!.stop()
                tmpDatabaseSrv!!.stop()
            }
        } catch(e: CmdLineException) {
            log.severe("No se han podido parsear los parametros. " + e.message)
        }
    }

    /**
     * Carga el dataset WW3 a la base de datos intermedia y trata los datos
     */
    fun loadWw3File(file: NetcdfFile) {
        log.info("Cargando el archivo WW3 a caché");

        val latDim: Variable? = file.findVariable("lat")
        val lonDim: Variable? = file.findVariable("lon")
        val timeDim: Variable? = file.findVariable("time")

        val dirmVar: Variable? = file.findVariable("dirm")
        val dirpVar: Variable? = file.findVariable("dirp")
        val tm10Var: Variable? = file.findVariable("tm-10")
        val rtpVar: Variable? = file.findVariable("rtp")

        if (latDim != null && lonDim != null && timeDim != null &&
                dirmVar != null && dirpVar != null && tm10Var != null && rtpVar != null) {
            var latArray = latDim.read() as ArrayFloat.D1

            var lonArray = lonDim.read() as ArrayFloat.D1

            var timeArray = timeDim.read() as ArrayInt.D1

            var dirmArray: ArrayFloat.D3 = dirmVar.read(IntArray(4), dirmVar.shape).reduce() as ArrayFloat.D3;
            var dirpArray: ArrayFloat.D3 = dirpVar.read(IntArray(4), dirpVar.shape).reduce() as ArrayFloat.D3;
            var tm10Array: ArrayFloat.D3 = tm10Var.read(IntArray(4), tm10Var.shape).reduce() as ArrayFloat.D3;
            var rtpArray: ArrayFloat.D3 = rtpVar.read(IntArray(4), rtpVar.shape).reduce() as ArrayFloat.D3;

            for (latIndex in 0..(latArray.size.toInt() - 1)) {
                for (lonIndex in 0..(lonArray.size.toInt() - 1)) {
                    for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                        var newdate = Date(startTime!!.time + (timeArray[timeIndex] * 110)) // TODO: Revisar
                        var record: Ww3Record = Ww3Record(SpatialPoint(latArray[latIndex].toDouble(), lonArray[lonIndex].toDouble()), newdate)

                        record.dirm = dirmArray.get(timeIndex, latIndex, lonIndex).toDouble()
                        record.dirp = dirpArray.get(timeIndex, latIndex, lonIndex).toDouble()
                        record.tm_10 = tm10Array.get(timeIndex, latIndex, lonIndex).toDouble()
                        record.rtp = rtpArray.get(timeIndex, latIndex, lonIndex).toDouble()

                        tmpDatabaseSrv!!.insert(record);
                    }
                }
            }

            log.info("El archivo WW3 ha sido cargado a caché");
        } else {
            log.severe("El archivo WW3 tiene un formato incorrecto y no se ha podido cargar en caché")
            throw IOException("Format incorrect")
        }
    }

    /**
     * Carga el dataset WRF a la base de datos intermedia y trata los datos
     */
    fun loadWrfFile(file: NetcdfFile) {
        log.info("Cargando el archivo WRF a caché");

        val xDim: Variable? = file.findVariable("x")
        val yDim: Variable? = file.findVariable("y")
        val timeDim: Variable? = file.findVariable("time")

        val latVar: Variable? = file.findVariable("lat")
        val lonVar: Variable? = file.findVariable("lon")
        val topoVar: Variable? = file.findVariable("topo")
        val tempVar: Variable? = file.findVariable("temp")
        val t500Var: Variable? = file.findVariable("T500")
        val t850Var: Variable? = file.findVariable("T850")
        val sstVar: Variable? = file.findVariable("sst")
        val cfhVar: Variable? = file.findVariable("cfh")
        val cfmVar: Variable? = file.findVariable("cfm")
        val cflVar: Variable? = file.findVariable("cfl")
        val visibilityVar: Variable? = file.findVariable("visibility")
        val snowlevelVar: Variable? = file.findVariable("snowlevel")
        val snowprecVar: Variable? = file.findVariable("snow_prec")
        val precVar: Variable? = file.findVariable("prec")
        val rhVar: Variable? = file.findVariable("rh")
        val dirVar: Variable? = file.findVariable("dir")
        val uVar: Variable? = file.findVariable("u")
        val vVar: Variable? = file.findVariable("v")
        val windgustVar: Variable? = file.findVariable("wind_gust")

        if (xDim != null && yDim != null && timeDim != null && latVar != null && lonVar != null &&
                topoVar != null && tempVar != null && t500Var != null && t850Var != null && sstVar != null &&
                cfhVar != null && cfmVar != null && cflVar != null && visibilityVar != null && snowlevelVar != null &&
                snowprecVar != null && precVar != null && rhVar != null && dirVar != null && uVar != null &&
                vVar != null && windgustVar != null) {
            var xArray = xDim.read() as ArrayDouble.D1
            var yArray = yDim.read() as ArrayDouble.D1
            var timeArray = timeDim.read() as ArrayInt.D1

            var latArray: ArrayFloat.D2 = latVar.read(IntArray(4), latVar.shape).reduce() as ArrayFloat.D2;
            var lonArray: ArrayFloat.D2 = lonVar.read(IntArray(4), lonVar.shape).reduce() as ArrayFloat.D2;
            var topoArray: ArrayFloat.D3 = topoVar.read(IntArray(4), topoVar.shape).reduce() as ArrayFloat.D3;
            var tempArray: ArrayFloat.D3 = tempVar.read(IntArray(4), tempVar.shape).reduce() as ArrayFloat.D3;
            var t500Array: ArrayFloat.D3 = t500Var.read(IntArray(4), t500Var.shape).reduce() as ArrayFloat.D3;
            var t850Array: ArrayFloat.D3 = t850Var.read(IntArray(4), t850Var.shape).reduce() as ArrayFloat.D3;
            var sstArray: ArrayFloat.D3 = sstVar.read(IntArray(4), sstVar.shape).reduce() as ArrayFloat.D3;
            var cfhArray: ArrayFloat.D3 = cfhVar.read(IntArray(4), cfhVar.shape).reduce() as ArrayFloat.D3;
            var cfmArray: ArrayFloat.D3 = cfmVar.read(IntArray(4), cfmVar.shape).reduce() as ArrayFloat.D3;
            var cflArray: ArrayFloat.D3 = cflVar.read(IntArray(4), cflVar.shape).reduce() as ArrayFloat.D3;
            var visibilityArray: ArrayFloat.D3 = visibilityVar.read(IntArray(4), visibilityVar.shape).reduce() as ArrayFloat.D3;
            var snowlevelArray: ArrayFloat.D3 = snowlevelVar.read(IntArray(4), snowlevelVar.shape).reduce() as ArrayFloat.D3;
            var snowprecArray: ArrayFloat.D3 = snowprecVar.read(IntArray(4), snowprecVar.shape).reduce() as ArrayFloat.D3;
            var precArray: ArrayFloat.D3 = precVar.read(IntArray(4), precVar.shape).reduce() as ArrayFloat.D3;
            var rhArray: ArrayFloat.D3 = rhVar.read(IntArray(4), rhVar.shape).reduce() as ArrayFloat.D3;
            var dirArray: ArrayFloat.D3 = dirVar.read(IntArray(4), dirVar.shape).reduce() as ArrayFloat.D3;
            var uArray: ArrayFloat.D3 = uVar.read(IntArray(4), uVar.shape).reduce() as ArrayFloat.D3;
            var vArray: ArrayFloat.D3 = vVar.read(IntArray(4), vVar.shape).reduce() as ArrayFloat.D3;
            var windgustArray: ArrayFloat.D3 = windgustVar.read(IntArray(4), windgustVar.shape).reduce() as ArrayFloat.D3;

            for (xIndex in 0..(xArray.size.toInt() - 1)) {
                for (yIndex in 0..(yArray.size.toInt() - 1)) {
                    for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                        var newdate = Date(startTime!!.time + (timeArray[timeIndex] * 165)) // TODO: Revisar
                        var record: WrfRecord = WrfRecord(SpatialPoint(latArray.get(xIndex, yIndex).toDouble(), lonArray.get(xIndex, yIndex).toDouble()), newdate)

                        record.topo = topoArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.temp = tempArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.t500 = t500Array.get(timeIndex, xIndex, yIndex).toDouble()
                        record.t850 = t850Array.get(timeIndex, xIndex, yIndex).toDouble()
                        record.sst = sstArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.chf = cfhArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.cfm = cfmArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.cfl = cflArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.visibility = visibilityArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.snow_level = snowlevelArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.snow_prec = snowprecArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.prec = precArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.humidity = rhArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.wind_dir = dirArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.wind_lon = uArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.wind_lat = vArray.get(timeIndex, xIndex, yIndex).toDouble()
                        record.wind_gust = windgustArray.get(timeIndex, xIndex, yIndex).toDouble()

                        tmpDatabaseSrv!!.insert(record);
                    }
                }
            }

            log.info("El archivo WRF ha sido cargado a caché");
        } else {
            log.severe("El archivo WRF tiene un formato incorrecto y no se ha podido cargar en caché")
            throw IOException("Format incorrect")
        }
    }

    /**
     * Carga el dataset Marine a la base de datos intermedia y trata los datos
     */
    fun loadMarineFile(file: NetcdfFile) {
        log.info("Cargando el archivo Marine a caché");

        val startTime: Date = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("1950-01-01 00:00:00")

        val latDim: Variable? = file.findVariable("latitude")
        val lonDim: Variable? = file.findVariable("longitude")
        val timeDim: Variable? = file.findVariable("time_counter")
        val depthDim: Variable? = file.findVariable("depth")

        val uVar: Variable? = file.findVariable("u")
        val vVar: Variable? = file.findVariable("v")
        val salinityVar: Variable? = file.findVariable("salinity")

        if (latDim != null && lonDim != null && timeDim != null && depthDim != null &&
                uVar != null && vVar != null && salinityVar != null) {
            var latArray = latDim.read() as ArrayFloat.D1
            var lonArray = lonDim.read() as ArrayFloat.D1
            var timeArray = timeDim.read() as ArrayFloat.D1
            var depthArray = depthDim.read() as ArrayFloat.D1

            var uArray: ArrayShort.D4 = uVar.read(IntArray(4), uVar.shape).reduce() as ArrayShort.D4;
            var vArray: ArrayShort.D4 = vVar.read(IntArray(4), vVar.shape).reduce() as ArrayShort.D4;
            var salinityArray: ArrayShort.D4 = salinityVar.read(IntArray(4), salinityVar.shape).reduce() as ArrayShort.D4;

            for (latIndex in 0..(latArray.size.toInt() - 1)) {
                for (lonIndex in 0..(lonArray.size.toInt() - 1)) {
                    for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                        for (depthIndex in 0..(depthArray.size.toInt() - 1)) {
                            var newdate = Date(startTime.time + (timeArray[timeIndex] * 60 * 60 * 1000).toLong())
                            var record: MarineRecord = MarineRecord(SpatialPoint(latArray[latIndex].toDouble(), lonArray[lonIndex].toDouble()), newdate, depthArray[depthIndex].toDouble())

                            record.u = uArray.get(timeIndex, depthIndex, latIndex, lonIndex).toInt()
                            record.v = vArray.get(timeIndex, depthIndex, latIndex, lonIndex).toInt()
                            record.salinity = salinityArray.get(timeIndex, depthIndex, latIndex, lonIndex).toInt()

                            tmpDatabaseSrv!!.insert(record);
                        }
                    }
                }
            }

            log.info("El archivo Marine ha sido cargado a caché");
        } else {
            log.severe("El archivo Marine tiene un formato incorrecto y no se ha podido cargar en caché")
            throw IOException("Format incorrect")
        }
    }

    /**
     * Carga los datos de la base de datos intermedia a la base de datos principal
     */
    fun loadData() {
        var dbIterator = tmpDatabaseSrv!!.selectIterator("wrf", WrfRecord());

        if (dbIterator == null) {
            log.severe("No hay elementos en la tabla wrf sobre los que iterar");
            return;
        }

        // Mientras siga habiendo datos en la base de datos intermedia
        while (dbIterator.hasNext()) {
            // Se obtienen los datos para la coordenada/tiempo actual
            val wrfRecord: WrfRecord = dbIterator.next()
            val ww3Record: Ww3Record = tmpDatabaseSrv!!.selectNearby("ww3", wrfRecord.position, wrfRecord.time, Ww3Record()) as Ww3Record
            val marineRecord: MarineRecord = tmpDatabaseSrv!!.selectNearby("marine", wrfRecord.position, wrfRecord.time, MarineRecord()) as MarineRecord

            // Se crea la dimension del tiempo
            val timeDimension: TimeRecord = TimeRecord();
            timeDimension.year = wrfRecord.time.year;
            timeDimension.month = wrfRecord.time.month;
            timeDimension.day = wrfRecord.time.date;
            timeDimension.hour = wrfRecord.time.hours;
            timeDimension.minute = wrfRecord.time.minutes;
            timeDimension.second = wrfRecord.time.seconds;

            // Se crea la dimension de la posición
            val locationDimension: LocationRecord = LocationRecord(wrfRecord.position.latitude, wrfRecord.position.longitude);

            // Se crea la dimension de las alertas
            val alertDimension: AlertRecord = AlertRecord();
            if (wrfRecord.temp!! > 41.0) alertDimension.temperature_high = true
            else if (wrfRecord.temp!! > 38.0) alertDimension.temperature_half = true
            else if (wrfRecord.temp!! > 35.0) alertDimension.temperature_low = true
            if (wrfRecord.prec!! > 120.0) alertDimension.rain_high = true
            else if (wrfRecord.prec!! > 80.0) alertDimension.rain_half = true
            else if (wrfRecord.prec!! > 40.0) alertDimension.rain_low = true

            // Se crea la clase principal
            val measurementTable: MeasurementRecord = MeasurementRecord();

            measurementTable.time = timeDimension;
            measurementTable.location = locationDimension;
            measurementTable.alert = alertDimension;

            measurementTable.cloud_cover_high = wrfRecord.chf;
            measurementTable.cloud_cover_half = wrfRecord.cfm;
            measurementTable.cloud_cover_low = wrfRecord.cfl;
            measurementTable.visibility = wrfRecord.visibility;

            measurementTable.elevation = wrfRecord.topo;

            measurementTable.temperature_sea_level = wrfRecord.sst;
            measurementTable.temperature_surface = wrfRecord.temp;
            measurementTable.temperature_500mb = wrfRecord.t500;
            measurementTable.temperature_850mb = wrfRecord.t850;

            measurementTable.salinity = marineRecord.salinity?.toDouble();
            measurementTable.water_speed_eastward = marineRecord.v?.toDouble();
            measurementTable.water_speed_northward = marineRecord.u?.toDouble();

            measurementTable.wave_direction_mean = ww3Record.dirm;
            measurementTable.wave_direction_peak = ww3Record.dirp;
            measurementTable.wave_period_absolute = ww3Record.rtp;
            measurementTable.wave_period_peak = ww3Record.tm_10;

            measurementTable.snow_level = wrfRecord.snow_level;
            measurementTable.snow_precipitation = wrfRecord.snow_prec;
            measurementTable.rain_precipitation = wrfRecord.prec;

            measurementTable.humidity = wrfRecord.humidity;

            measurementTable.wind_direction = wrfRecord.wind_dir;
            measurementTable.wind_lat = wrfRecord.wind_lat;
            measurementTable.wind_lon = wrfRecord.wind_lon;
            measurementTable.wind_gust = wrfRecord.wind_gust;

            // Se guarda el registro actual en la base de datos
            warehouseDatabaseSrv!!.insert(measurementTable);
        }
    }
}

fun main(args : Array<String>) {
    var init = SILoaderMain()
    init.main(args)
}