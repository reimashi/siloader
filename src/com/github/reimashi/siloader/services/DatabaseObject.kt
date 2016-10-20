package com.github.reimashi.siloader.services

public interface DatabaseObject {
    fun getTable(): String
    fun getFields(): Map<String, Any?>
}