package com.github.reimashi.siloader;

import com.github.reimashi.siloader.services.CacheService
import com.github.reimashi.siloader.services.DatabaseService
import ucar.ma2.ArrayFloat
import ucar.ma2.ArrayInt
import ucar.nc2.NetcdfFile
import ucar.nc2.Variable
import java.io.IOException
import java.util.logging.Logger

val log = Logger.getLogger("Main")

fun main(args: Array<String>) {
    val ww3Path: String = "ww3.nc"
    val wrfPath: String = "wrf.nc4"
    val marinePath: String = "marine.nc"

    var ww3Ncfile: NetcdfFile? = null
    var wrfNcfile: NetcdfFile? = null
    var marineNcfile: NetcdfFile? = null

    val config: Configuration = Configuration()
    val cacheSrv: CacheService = CacheService(config)
    val databaseSrv: DatabaseService = DatabaseService(config)

    try {
        ww3Ncfile = NetcdfFile.openInMemory(ww3Path)
        wrfNcfile = NetcdfFile.openInMemory(wrfPath)
        marineNcfile = NetcdfFile.openInMemory(marinePath)

        loadWw3File(ww3Ncfile);
        loadWrfFile(wrfNcfile);
        loadMarineFile(marineNcfile);
    } catch (ioe: IOException) {
        System.out.println("Error al abrir un fichero de entrada. " + ioe.message)
    } finally {
        try {
            ww3Ncfile?.close()
            wrfNcfile?.close()
            marineNcfile?.close()
        }
        catch (ioe: IOException) {
            System.out.println("Error al cerrar los ficheros de entrada. " + ioe.message)
        }
    }
}

fun loadWw3File(file: NetcdfFile) {
    log.info("Cargando el archivo WW3 a caché");

    val latDim: Variable? = file.findVariable("lat")
    val lonDim: Variable? = file.findVariable("lon")
    val timeDim: Variable? = file.findVariable("time")

    val u10Var: Variable? = file.findVariable("u10")
    val v10Var: Variable? = file.findVariable("v10")
    val hsVar: Variable? = file.findVariable("hs")
    val hswellVar: Variable? = file.findVariable("hswell")
    val hswindVar: Variable? = file.findVariable("hswind")
    val dirmVar: Variable? = file.findVariable("dirm")
    val dirpVar: Variable? = file.findVariable("dirp")
    val tm10Var: Variable? = file.findVariable("tm-10")
    val rtpVar: Variable? = file.findVariable("rtp")

    if (latDim != null && lonDim != null && timeDim != null &&
            u10Var != null && v10Var != null && hsVar != null && hswellVar != null && hswindVar != null &&
            dirmVar != null && dirpVar != null && tm10Var != null && rtpVar != null)
    {
        var latArray = latDim.read() as ArrayFloat.D1

        var lonArray = lonDim.read() as ArrayFloat.D1

        var timeArray = timeDim.read() as ArrayInt.D1

        var u10Array: ArrayFloat.D3 = u10Var.read(IntArray(4), u10Var.shape).reduce() as ArrayFloat.D3;
        var v10Array: ArrayFloat.D3 = v10Var.read(IntArray(4), v10Var.shape).reduce() as ArrayFloat.D3;
        var hsVarArray: ArrayFloat.D3 = hsVar.read(IntArray(4), hsVar.shape).reduce() as ArrayFloat.D3;
        var hswellArray: ArrayFloat.D3 = hswellVar.read(IntArray(4), hswellVar.shape).reduce() as ArrayFloat.D3;
        var hswindArray: ArrayFloat.D3 = hswindVar.read(IntArray(4), hswindVar.shape).reduce() as ArrayFloat.D3;
        var dirmArray: ArrayFloat.D3 = dirmVar.read(IntArray(4), dirmVar.shape).reduce() as ArrayFloat.D3;
        var dirpArray: ArrayFloat.D3 = dirpVar.read(IntArray(4), dirpVar.shape).reduce() as ArrayFloat.D3;
        var tm10Array: ArrayFloat.D3 = tm10Var.read(IntArray(4), tm10Var.shape).reduce() as ArrayFloat.D3;
        var rtpArray: ArrayFloat.D3 = rtpVar.read(IntArray(4), rtpVar.shape).reduce() as ArrayFloat.D3;

        for (latIndex in 0..(latArray.size.toInt() - 1)) {
            for (lonIndex in 0..(lonArray.size.toInt() - 1)) {
                for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                    //System.out.println(u10Array.get(timeIndex, latIndex, lonIndex));
                }
            }
        }

        log.info("El archivo WW3 ha sido cargado a caché");
    }
    else {
        log.severe("El archivo WW3 tiene un formato incorrecto y no se ha podido cargar en caché")
    }
}

fun loadWrfFile(file: NetcdfFile) {
    log.info("Cargando el archivo WRF a caché");
    System.out.println(file.variables.map { it.name });
    val xDim: Variable? = file.findVariable("x")
    val yDim: Variable? = file.findVariable("y")
    val timeDim: Variable? = file.findVariable("time")

    val u10Var: Variable? = file.findVariable("u10")
    val v10Var: Variable? = file.findVariable("v10")
    val hsVar: Variable? = file.findVariable("hs")
    val hswellVar: Variable? = file.findVariable("hswell")
    val hswindVar: Variable? = file.findVariable("hswind")
    val dirmVar: Variable? = file.findVariable("dirm")
    val dirpVar: Variable? = file.findVariable("dirp")
    val tm10Var: Variable? = file.findVariable("tm-10")
    val rtpVar: Variable? = file.findVariable("rtp")

    if (xDim != null && yDim != null && timeDim != null &&
            u10Var != null && v10Var != null && hsVar != null && hswellVar != null && hswindVar != null &&
            dirmVar != null && dirpVar != null && tm10Var != null && rtpVar != null)
    {
        var xArray = xDim.read() as ArrayFloat.D1
        var yArray = yDim.read() as ArrayFloat.D1
        var timeArray = timeDim.read() as ArrayInt.D1

        var u10Array: ArrayFloat.D3 = u10Var.read(IntArray(4), u10Var.shape).reduce() as ArrayFloat.D3;
        var v10Array: ArrayFloat.D3 = v10Var.read(IntArray(4), v10Var.shape).reduce() as ArrayFloat.D3;
        var hsVarArray: ArrayFloat.D3 = hsVar.read(IntArray(4), hsVar.shape).reduce() as ArrayFloat.D3;
        var hswellArray: ArrayFloat.D3 = hswellVar.read(IntArray(4), hswellVar.shape).reduce() as ArrayFloat.D3;
        var hswindArray: ArrayFloat.D3 = hswindVar.read(IntArray(4), hswindVar.shape).reduce() as ArrayFloat.D3;
        var dirmArray: ArrayFloat.D3 = dirmVar.read(IntArray(4), dirmVar.shape).reduce() as ArrayFloat.D3;
        var dirpArray: ArrayFloat.D3 = dirpVar.read(IntArray(4), dirpVar.shape).reduce() as ArrayFloat.D3;
        var tm10Array: ArrayFloat.D3 = tm10Var.read(IntArray(4), tm10Var.shape).reduce() as ArrayFloat.D3;
        var rtpArray: ArrayFloat.D3 = rtpVar.read(IntArray(4), rtpVar.shape).reduce() as ArrayFloat.D3;

        for (xIndex in 0..(xArray.size.toInt() - 1)) {
            for (yIndex in 0..(yArray.size.toInt() - 1)) {
                for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                    //System.out.println(u10Array.get(timeIndex, latIndex, lonIndex));
                }
            }
        }

        log.info("El archivo WRF ha sido cargado a caché");
    }
    else {
        log.severe("El archivo WRF tiene un formato incorrecto y no se ha podido cargar en caché")
    }
}

fun loadMarineFile(file: NetcdfFile) {
    log.info("Cargando el archivo Marine a caché");

    System.out.println(file.variables.map { it.name });

    val xDim: Variable? = file.findVariable("x")
    val yDim: Variable? = file.findVariable("y")
    val timeDim: Variable? = file.findVariable("time")

    val u10Var: Variable? = file.findVariable("u10")
    val v10Var: Variable? = file.findVariable("v10")
    val hsVar: Variable? = file.findVariable("hs")
    val hswellVar: Variable? = file.findVariable("hswell")
    val hswindVar: Variable? = file.findVariable("hswind")
    val dirmVar: Variable? = file.findVariable("dirm")
    val dirpVar: Variable? = file.findVariable("dirp")
    val tm10Var: Variable? = file.findVariable("tm-10")
    val rtpVar: Variable? = file.findVariable("rtp")

    if (xDim != null && yDim != null && timeDim != null &&
            u10Var != null && v10Var != null && hsVar != null && hswellVar != null && hswindVar != null &&
            dirmVar != null && dirpVar != null && tm10Var != null && rtpVar != null)
    {
        var xArray = xDim.read() as ArrayFloat.D1
        var yArray = yDim.read() as ArrayFloat.D1
        var timeArray = timeDim.read() as ArrayInt.D1

        var u10Array: ArrayFloat.D3 = u10Var.read(IntArray(4), u10Var.shape).reduce() as ArrayFloat.D3;
        var v10Array: ArrayFloat.D3 = v10Var.read(IntArray(4), v10Var.shape).reduce() as ArrayFloat.D3;
        var hsVarArray: ArrayFloat.D3 = hsVar.read(IntArray(4), hsVar.shape).reduce() as ArrayFloat.D3;
        var hswellArray: ArrayFloat.D3 = hswellVar.read(IntArray(4), hswellVar.shape).reduce() as ArrayFloat.D3;
        var hswindArray: ArrayFloat.D3 = hswindVar.read(IntArray(4), hswindVar.shape).reduce() as ArrayFloat.D3;
        var dirmArray: ArrayFloat.D3 = dirmVar.read(IntArray(4), dirmVar.shape).reduce() as ArrayFloat.D3;
        var dirpArray: ArrayFloat.D3 = dirpVar.read(IntArray(4), dirpVar.shape).reduce() as ArrayFloat.D3;
        var tm10Array: ArrayFloat.D3 = tm10Var.read(IntArray(4), tm10Var.shape).reduce() as ArrayFloat.D3;
        var rtpArray: ArrayFloat.D3 = rtpVar.read(IntArray(4), rtpVar.shape).reduce() as ArrayFloat.D3;

        for (xIndex in 0..(xArray.size.toInt() - 1)) {
            for (yIndex in 0..(yArray.size.toInt() - 1)) {
                for (timeIndex in 0..(timeArray.size.toInt() - 1)) {
                    //System.out.println(u10Array.get(timeIndex, latIndex, lonIndex));
                }
            }
        }

        log.info("El archivo Marine ha sido cargado a caché");
    }
    else {
        log.severe("El archivo Marine tiene un formato incorrecto y no se ha podido cargar en caché")
    }
}