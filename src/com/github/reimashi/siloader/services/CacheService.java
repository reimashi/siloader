package com.github.reimashi.siloader.services;

import com.github.reimashi.siloader.Configuration;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.apache.commons.codec.binary.Base64;

import java.io.*;

public class CacheService {
    Configuration config = null;
    RedisClient redisClient = null;
    StatefulRedisConnection<String, String> connection = null;
    RedisCommands<String, String> syncCommands = null;

    public CacheService(Configuration config) {
        redisClient = RedisClient.create(config.getCacheUrl());
    }

    public void start() {
        if (this.connection == null) {
            this.connection = redisClient.connect();
            this.syncCommands = connection.sync();
        }
    }

    /**
     * Para el servicio Database
     */
    public void stop() {
        if (this.connection != null) {
            this.connection.close();
            this.connection = null;
        }
    }

    public String get(String key) { return this.syncCommands.get(key); }
    public Object getObject(String key) throws IOException, ClassNotFoundException { return fromString(this.syncCommands.get(key)); }
    public String set(String key, String value) { return this.syncCommands.set(key, value); }
    public String setObject(String key, Serializable value) throws IOException { return this.syncCommands.set(key, objectToString(value)); }

    /**
     * Convierte un objeto serializado a string en base64 en objeto
     * @param s
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Object fromString( String s ) throws IOException,
            ClassNotFoundException {
        byte [] data = Base64.decodeBase64( s );
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Convierte un objeto en un objeto serializado a string en base64
     * @param o
     * @return
     * @throws IOException
     */
    private static String objectToString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String(Base64.encodeBase64(baos.toByteArray()));
    }
}
