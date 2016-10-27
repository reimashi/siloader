package com.github.reimashi.siloader.services

import java.sql.ResultSet
import java.util.*

class DatabaseObjectIterator<T : DatabaseObject>(val rel: ResultSet, val fields: List<String>, val factory: () -> T) : Iterator<T> {
    private var nextReaded = false;
    private var hasNext = false;

    constructor(rel: ResultSet, fields: List<String>, default: T) : this(rel, fields, { default })

    override fun next(): T {
        if (!nextReaded) { nextReaded = true; hasNext = rel.next(); }

        if (this.hasNext()) {
            var loadElems: HashMap<String, String?> = HashMap<String, String?>();

            for (key in this.fields) {
                loadElems.put(key, rel.getString(key));
            }

            var default: T = factory();
            default.loadValues(loadElems);
            return default;
        }
        else throw IndexOutOfBoundsException("Iterable object don't have more elements.");
    }

    override fun hasNext(): Boolean {
        if (!nextReaded) { nextReaded = true; hasNext = rel.next(); }
        return hasNext;
    }
}