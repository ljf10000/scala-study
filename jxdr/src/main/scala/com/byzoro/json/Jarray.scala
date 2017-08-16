package com.byzoro.json

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

object Jarray {
    def apply(jstr: String): JSONArray = try {
        JSON.parseArray(jstr)
    } catch {
        case _: Any => null
    }

    def jbool(j: JSONArray, idx: Int): Boolean = try {
        j.getBooleanValue(idx)
    } catch {
        case _: Any => false
    }

    def jint(j: JSONArray, idx: Int): Int = try {
        j.getIntValue(idx)
    } catch {
        case _: Any => 0
    }

    def jlong(j: JSONArray, idx: Int): Long = try {
        j.getLongValue(idx)
    } catch {
        case _: Any => 0
    }

    def jdouble(j: JSONArray, idx: Int): Double = try {
        j.getDoubleValue(idx)
    } catch {
        case _: Any => 0.0
    }

    def jstring(j: JSONArray, idx: Int): String = try {
        j.getString(idx)
    } catch {
        case _: Any => ""
    }

    def jobject(j: JSONArray, idx: Int): JSONObject = try {
        j.getJSONObject(idx)
    } catch {
        case _: Any => null
    }

    def jarray(j: JSONArray, idx: Int): JSONArray = try {
        j.getJSONArray(idx)
    } catch {
        case _: Any => null
    }

    def jexception(s: String): Exception = new Exception(s)
}
