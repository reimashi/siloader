package com.github.reimashi.siloader.services

public interface DatabaseObject {
    abstract fun getTable(): String
    abstract fun getFields(): Map<String, Any?>
    abstract fun loadValues(values: Map<String, String?>)
}