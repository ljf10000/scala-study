package com.byzoro.json

import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}

object Jobject {
    def apply(jstr: String): JSONObject = try {
        JSON.parseObject(jstr)
    } catch {
        case _: Any => null
    }

    def jbool(j: JSONObject, k: String): Boolean = try {
        j.getBooleanValue(k)
    } catch {
        case _: Any => false
    }

    def jint(j: JSONObject, k: String): Int = try {
        j.getIntValue(k)
    } catch {
        case _: Any => 0
    }

    def jlong(j: JSONObject, k: String): Long = try {
        j.getLongValue(k)
    } catch {
        case _: Any => 0
    }

    def jdouble(j: JSONObject, k: String): Double = try {
        j.getDoubleValue(k)
    } catch {
        case _: Any => 0.0
    }

    def jstring(j: JSONObject, k: String): String = try {
        j.getString(k)
    } catch {
        case _: Any => ""
    }

    def jobject(j: JSONObject, k: String): JSONObject = try {
        j.getJSONObject(k)
    } catch {
        case _: Any => null
    }

    def jarray(j: JSONObject, k: String): JSONArray = try {
        j.getJSONArray(k)
    } catch {
        case _: Any => null
    }

    def jexception(s: String): Exception = new Exception(s)
}
