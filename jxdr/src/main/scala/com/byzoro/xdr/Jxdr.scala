package com.byzoro.xdr

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.byzoro.json.{Jobject, Jarray}
import org.apache.spark.sql.types._

class Jxdr {

    import Jobject._

    type int8_t = Int
    type int16_t = Int
    type int32_t = Int
    type int64_t = Long

    type uint8_t = Int
    type uint16_t = Int
    type uint32_t = Int
    type uint64_t = Long

    type ipaddr_t = uint32_t
    type delay_t = uint32_t
    type offset_t = uint32_t
    type bkdr_t = uint32_t

    type duration_t = uint64_t
    type time_t = uint64_t

    case class SmallFile(dbname: String, tbname: String, signature: String)

    private def j2smallfile(j: JSONObject): SmallFile = {
        SmallFile(dbname = jstring(j, "DbName"),
            tbname = jstring(j, "TableName"),
            signature = jstring(j, "Signature"))
    }

    case class BigFile(file: String, size: uint32_t, offset: offset_t, signature: String)

    private def j2bigfile(j: JSONObject): BigFile = {
        BigFile(file = jstring(j, "File"),
            size = jint(j, "Size"),
            offset = jint(j, "Offset"),
            signature = jstring(j, "Signature"))
    }

    case class Cert(file: SmallFile,
                    version: uint8_t,
                    serial_number: String,
                    key_usage: uint16_t,
                    not_before: time_t,
                    not_after: time_t,
                    country_name: String,
                    organization_name: String,
                    organization_unit_name: String,
                    common_name: String)

    private def j2cert(j: JSONObject): Cert = {
        val file = jobject(j, "FileLocation")
        if (null == file) {
            throw jexception("cert without file")
        }

        Cert(file = j2smallfile(file),
            version = jint(j, "Version"),
            serial_number = jstring(j, "SerialNumber"),
            key_usage = jint(j, "KeyUsage"),
            not_before = jlong(j, "NotBefore"),
            not_after = jlong(j, "NotAfter"),
            country_name = jstring(j, "CountryName"),
            organization_name = jstring(j, "OrganizationName"),
            organization_unit_name = jstring(j, "OrganizationUnitName"),
            common_name = jstring(j, "CommonName"))
    }

    private def j2certs(j: JSONArray): Array[Cert] = {
        val count = j.size()
        val certs = new Array[Cert](count)

        for (i <- 0 until count) {
            certs(i) = j2cert(Jarray.jobject(j, i))
        }

        certs
    }

    case class SslEndpoint(verfy: Boolean,
                           verfy_failed_desc: String,
                           verfy_failed_idx: uint8_t,
                           cert: Cert,
                           certs: Array[Cert])

    private def j2ssl_endpoint(j: JSONObject): SslEndpoint = {
        val cert = jobject(j, "Cert")
        if (null == cert) {
            throw jexception("ssl end point without cert")
        }

        val certs = jarray(j, "Certs")
        if (null == certs) {
            throw jexception("ssl end point without certs")
        }

        SslEndpoint(
            verfy = jbool(j, "Verfy"),
            verfy_failed_desc = jstring(j, "VerfyFailedDesc"),
            verfy_failed_idx = jint(j, "VerfyFailedIdx"),
            cert = j2cert(cert),
            certs = j2certs(certs))
    }

    case class Conn(proto: uint8_t,
                    sport: uint16_t,
                    dport: uint16_t,
                    sip: String,
                    dip: String)

    private def j2Conn(j: JSONObject): Unit = {
        if (null == j) {
            throw jexception("without conn")
        }

        val proto = jint(j, "Proto")

        conn = Conn(
            proto = proto,
            sport = jint(j, "Sport"),
            dport = jint(j, "Dport"),
            sip = jstring(j, "Sip"),
            dip = jstring(j, "Dip"))

        ip_proto = proto
    }

    case class ConnTm(start: time_t, stop: time_t)

    private def j2ConnTm(j: JSONObject): Unit = {
        if (null == j) {
            throw jexception("without conn time")
        }

        conn_tm = ConnTm(
            start = jlong(j, "Start"),
            stop = jlong(j, "End"))
    }

    case class ConnSt(flow_up: uint32_t,
                      flow_down: uint32_t,
                      ip_packet_up: uint32_t,
                      ip_packet_down: uint32_t,
                      ip_frag_up: uint32_t,
                      ip_frag_down: uint32_t)

    private def j2ConnSt(j: JSONObject): Unit = {
        if (null == j) {
            throw jexception("without conn st")
        }

        conn_st = ConnSt(
            flow_up = jint(j, "FlowUp"),
            flow_down = jint(j, "FlowDown"),
            ip_packet_up = jint(j, "PktUp"),
            ip_packet_down = jint(j, "PktDown"),
            ip_frag_up = jint(j, "IpFragUp"),
            ip_frag_down = jint(j, "IpFragDown"))

    }

    case class Tcp(disorder_up: uint32_t,
                   disorder_down: uint32_t,
                   retran_up: uint32_t,
                   retran_down: uint32_t,
                   synack_to_syn_time: uint16_t,
                   ack_to_syn_time: uint16_t,
                   report_flag: uint8_t,
                   close_reason: uint8_t,
                   first_request_delay: delay_t,
                   first_response_delay: delay_t,
                   window: uint32_t,
                   mss: uint16_t,
                   count_retry: uint8_t,
                   count_retry_ack: uint8_t,
                   count_ack: uint8_t,
                   connect_success: Boolean,
                   handshake12_success: Boolean,
                   handshake23_success: Boolean,
                   open_status: Int,
                   close_status: Int) {
        def is_complete(): Boolean = {
            Jxdr.const.tcp.complete == this.report_flag
        }
    }

    private def j2Tcp(j: JSONObject): Unit = {
        if (null != j) {
            tcp = Some(Tcp(
                disorder_up = jint(j, "DisorderUp"),
                disorder_down = jint(j, "DisorderDown"),
                retran_up = jint(j, "RetranUp"),
                retran_down = jint(j, "RetranDown"),
                synack_to_syn_time = jint(j, "SynAckDelay"),
                ack_to_syn_time = jint(j, "AckDelay"),
                report_flag = jint(j, "ReportFlag"),
                close_reason = jint(j, "CloseReason"),
                first_request_delay = jint(j, "FirstRequestDelay"),
                first_response_delay = jint(j, "FirstResponseDely"),
                window = jint(j, "Window"),
                mss = jint(j, "Mss"),
                count_retry = jint(j, "SynCount"),
                count_retry_ack = jint(j, "SynAckCount"),
                count_ack = jint(j, "AckCount"),
                connect_success = jbool(j, "SessionOK"),
                handshake12_success = jbool(j, "Handshake12"),
                handshake23_success = jbool(j, "Handshake23"),
                open_status = jint(j, "Open"),
                close_status = jint(j, "Close")))
        }
    }

    // not use json Request/Response
    case class Http(host: String,
                    url: String,
                    host_xonline: String,
                    user_agent: String,
                    content_type: String,
                    refer: String,
                    cookie: String,
                    location: String,
                    file_request: Option[BigFile],
                    file_response: Option[BigFile],
                    time_request: time_t,
                    time_first_response: time_t,
                    time_last_content: time_t,
                    service_delay: duration_t,
                    content_length: uint32_t,
                    status_code: uint16_t,
                    method: uint8_t,
                    version: uint8_t,
                    head_flag: Boolean,
                    serv_flag: uint8_t,
                    first_request: Boolean,
                    brower: uint8_t,
                    portal: uint8_t)

    private def j2Http(j: JSONObject): Unit = {
        if (null != j) {
            val file_request = jobject(j, "RequestLocation")
            val file_response = jobject(j, "ResponseLocation")

            http = Some(Http(
                host = jstring(j, "Host"),
                url = jstring(j, "Url"),
                host_xonline = jstring(j, "XonlineHost"),
                user_agent = jstring(j, "UserAgent"),
                content_type = jstring(j, "ContentType"),
                refer = jstring(j, "Refer"),
                cookie = jstring(j, "Cookie"),
                location = jstring(j, "Location"),
                file_request = if (null == file_request) None else Some(j2bigfile(file_request)),
                file_response = if (null == file_response) None else Some(j2bigfile(file_response)),
                time_request = jlong(j, "RequestTime"),
                time_first_response = jlong(j, "FirstResponseTime"),
                time_last_content = jlong(j, "LastContentTime"),
                service_delay = jlong(j, "ServTime"),
                content_length = jint(j, "ContentLen"),
                status_code = jint(j, "StateCode"),
                method = jint(j, "Method"),
                version = jint(j, "Version"),
                head_flag = jbool(j, "HeadFlag"),
                serv_flag = jint(j, "ServFlag"),
                first_request = jbool(j, "RequestFlag"),
                brower = jint(j, "Browser"),
                portal = jint(j, "Portal")))
        }
    }

    case class Sip(calling_number: String,
                   called_number: String,
                   session_id: String,
                   call_direction: uint8_t,
                   call_type: uint8_t,
                   hangup_reason: uint8_t,
                   signal_type: uint8_t,
                   stream_count: uint16_t,
                   malloc: Boolean,
                   bye: Boolean,
                   invite: Boolean)

    private def j2Sip(j: JSONObject): Unit = {
        if (null != j) {
            sip = Some(Sip(
                calling_number = jstring(j, "CallingNo"),
                called_number = jstring(j, "CalledNo"),
                session_id = jstring(j, "SessionId"),
                call_direction = jint(j, "CallDir"),
                call_type = jint(j, "CallType"),
                hangup_reason = jint(j, "HangupReason"),
                signal_type = jint(j, "SignalType"),
                stream_count = jint(j, "StreamCount"),
                malloc = jbool(j, "Malloc"),
                bye = jbool(j, "Bye"),
                invite = jbool(j, "Invite")))
        }
    }

    case class Rtsp(url: String,
                    user_agent: String,
                    server_ip: String,
                    port_client_start: uint16_t,
                    port_client_end: uint16_t,
                    port_server_start: uint16_t,
                    port_server_end: uint16_t,
                    count_video: uint16_t,
                    count_audio: uint16_t,
                    describe_delay: uint32_t)

    private def j2Rtsp(j: JSONObject): Unit = {
        if (null != j) {
            rtsp = Some(Rtsp(
                url = jstring(j, "Url"),
                user_agent = jstring(j, "UserAgent"),
                server_ip = jstring(j, "ServerIp"),
                port_client_start = jint(j, "ClientBeginPort"),
                port_client_end = jint(j, "ClientEndPort"),
                port_server_start = jint(j, "ServerBeginPort"),
                port_server_end = jint(j, "ServerEndPort"),
                count_video = jint(j, "VideoStreamCount"),
                count_audio = jint(j, "AudeoStreamCount"),
                describe_delay = jint(j, "ResDelay")))
        }
    }

    case class Ftp(state: uint16_t,
                   user: String,
                   pwd: String,
                   trans_mode: uint8_t,
                   trans_type: uint8_t,
                   filesize: uint32_t,
                   response_delay: duration_t,
                   trans_duration: duration_t)

    private def j2Ftp(j: JSONObject): Unit = {
        if (null != j) {
            ftp = Some(Ftp(
                state = jint(j, "State"),
                user = jstring(j, "User"),
                pwd = jstring(j, "CurrentDir"),
                trans_mode = jint(j, "TransMode"),
                trans_type = jint(j, "TransType"),
                filesize = jint(j, "FileSize"),
                response_delay = jlong(j, "RspTm"),
                trans_duration = jlong(j, "TransTm")))
        }
    }

    case class Mail(msg_type: uint16_t,
                    status_code: int16_t,
                    length: uint32_t,
                    acs_type: uint8_t,
                    user: String,
                    domain: String,
                    sender: String,
                    recver: String,
                    hdr: String)

    private def j2Mail(j: JSONObject): Unit = {
        if (null != j) {
            mail = Some(Mail(
                msg_type = jint(j, "MsgType"),
                status_code = jint(j, "RspState"),
                length = jint(j, "Len"),
                acs_type = jint(j, "AcsType"),
                user = jstring(j, "UserName"),
                domain = jstring(j, "DomainInfo"),
                sender = jstring(j, "RecverInfo"),
                recver = jstring(j, "RecvAccount"),
                hdr = jstring(j, "Hdr")))
        }
    }

    case class Dns(response_code: uint8_t,
                   count_request: uint8_t,
                   count_response_record: uint8_t,
                   count_response_auth: uint8_t,
                   count_response_extra: uint8_t,
                   ip_version: uint8_t,
                   ip_count: uint8_t,
                   delay: delay_t,
                   domain: String,
                   ip: String,
                   ips: Array[String])

    private def j2dns_ips(j: JSONArray): Array[String] = {
        if (null == j) {
            throw jexception("dns without ips")
        }

        val count = j.size()
        val ips = new Array[String](count)

        for (i <- 0 until count) {
            ips(i) = j.getString(i)
        }

        ips
    }

    private def j2Dns(j: JSONObject): Unit = {
        if (null != j) {
            dns = Some(Dns(
                response_code = jint(j, "RspCode"),
                count_request = jint(j, "ReqCount"),
                count_response_record = jint(j, "RspRecordCount"),
                count_response_auth = jint(j, "AuthCnttCount"),
                count_response_extra = jint(j, "ExtraRecordCount"),
                ip_version = jint(j, "IpVersion"),
                ip_count = jint(j, "IpCount"),
                delay = jint(j, "HeadFlag"),
                domain = jstring(j, "Domain"),
                ip = jstring(j, "Ip"),
                ips = j2dns_ips(jarray(j, "Ips"))))
        }
    }

    case class Ssl(reason: uint8_t,
                   server: Option[SslEndpoint],
                   client: Option[SslEndpoint])

    private def j2Ssl(j: JSONObject): Unit = {
        if (null != j) {
            val server = jobject(j, "Server")
            val client = jobject(j, "Client")

            ssl = Some(Ssl(
                reason = jint(j, "FailReason"),
                server = if (null == server) None else Some(j2ssl_endpoint(server)),
                client = if (null == client) None else Some(j2ssl_endpoint(client))))
        }
    }

    // not use json ProtoInfo/ClassId/Proto
    case class App(status: uint8_t, local: String, file: Option[BigFile])

    private def j2App(j: JSONObject): Unit = {
        if (null != j) {
            val file = jobject(j, "FileLocation")

            app = Some(App(
                status = Jobject.jint(j, "Status"),
                local = Jobject.jstring(j, "File"),
                file = if (null == file) None else Some(j2bigfile(file))))
        }
    }

    private def j2Root(j: JSONObject): Unit = {
        vender = Jobject.jstring(j, "Vendor")
        id = Jobject.jint(j, "Id")
        ipv4 = Jobject.jbool(j, "Ipv4")
        appid = Jobject.jint(j, "Class")
        Type = Jobject.jint(j, "Type")
        time = Jobject.jlong(j, "Time")
    }

    // not use json ConnEx/ServSt/Vpn/Proxy/QQ

    // not in json
    var ip_proto: uint8_t = Jxdr.const.conn.proto.tcp

    var vender: String = _
    var id: Int = _
    var ipv4: Boolean = true
    var appid: uint8_t = _
    var Type: Int = Jxdr.const.Type.normal
    var time: time_t = _

    var conn: Conn = _
    var conn_tm: ConnTm = _
    var conn_st: ConnSt = _
    var tcp: Option[Tcp] = _
    var http: Option[Http] = _
    var sip: Option[Sip] = _
    var rtsp: Option[Rtsp] = _
    var ftp: Option[Ftp] = _
    var mail: Option[Mail] = _
    var dns: Option[Dns] = _
    var ssl: Option[Ssl] = _
    var app: Option[App] = _
}

object Jxdr {
    object const {

        object tcp {
            val complete = 1

            object open {
                val sucess = 0
                val syn = 1
                val synack = 2
            }

            object close {
                val fin = 0
                val client_rst = 1
                val server_rst = 2
                val timeout = 3
            }

        }

        // type
        object Type {
            val normal = 0
            val file = 1
            val http = 2
            val cert = 3
        }

        object conn {

            object proto {
                val icmp = 1
                val tcp = 6
                val udp = 17
            }

        }

        object appid {
            val common = 100
            val dns = 101
            val mms = 102
            val http = 103
            val ftp = 104
            val mail = 105
            val sip = 106
            val rtsp = 107
            val p2p = 108
            val video = 109
            val im = 110
            val ssl = 111
        }

        // app.status
        object app {

            object status {
                val sucess = 0
                val syn = 1
                val syn_ack = 2
                val timeout = 3
                val server_reject = 4
                val client_reject = 5
                val server_rst = 6
                val client_rst = 7
            }

        }

        object http {

            object version {
                val http_09 = 1
                val http_10 = 2
                val http_11 = 3
                val http_20 = 4
                val wap_10 = 5
                val wap_11 = 6
                val wap_12 = 7
            }

            object service {
                val sucess = 0
                val fail = 1
                val unknow = 2
                val complete = 3
            }

            object portal {
                val tencent = 1
                val taobao = 2
                val sina = 3
                val N163 = 4
                val mobile = 5
                val sohu = 6
                val ifeng = 7
                val baidu = 8
            }

            object browser {
                val uc = 1
                val qq = 2
                val N360 = 3
                val skyfire = 4
                val ubuntu = 5
                val baidu = 6
                val chrome = 7
                val firefox = 8
                val opera = 9
                val safari = 10
            }

        }

    }

    object schema {
        val bigfile = StructType(List(
            StructField("File", DataTypes.StringType, false),
            StructField("Size", DataTypes.IntegerType, false),
            StructField("Offset", DataTypes.IntegerType, false),
            StructField("Signature", DataTypes.StringType, false)
        ))

        val smallfile = StructType(List(
            StructField("DbName", DataTypes.StringType, false),
            StructField("TableName", DataTypes.StringType, false),
            StructField("Signature", DataTypes.StringType, false)
        ))

        val cert = StructType(List(
            StructField("FileLocation", smallfile, false),

            StructField("Version", DataTypes.IntegerType),
            StructField("SerialNumber", DataTypes.StringType),
            StructField("KeyUsage", DataTypes.IntegerType),
            StructField("NotBefore", DataTypes.LongType),
            StructField("NotAfter", DataTypes.LongType),
            StructField("CountryName", DataTypes.StringType),
            StructField("OrganizationName", DataTypes.StringType),
            StructField("OrganizationUnitName", DataTypes.StringType),
            StructField("CommonName", DataTypes.StringType)
        ))

        val certs: ArrayType = DataTypes.createArrayType(cert, false)

        val ssl_endpoint = StructType(List(
            StructField("Verfy", DataTypes.BooleanType, false),
            StructField("VerfyFailedDesc", DataTypes.StringType),
            StructField("VerfyFailedIdx", DataTypes.IntegerType),
            StructField("Cert", cert, false),
            StructField("Certs", certs, false)
        ))

        val conn = StructType(List(
            StructField("Proto", DataTypes.IntegerType, false),
            StructField("Sport", DataTypes.IntegerType, false),
            StructField("Dport", DataTypes.IntegerType, false),
            StructField("Sip", DataTypes.StringType, false),
            StructField("Dip", DataTypes.StringType, false)
        ))

        val conn_tm = StructType(List(
            StructField("Start", DataTypes.LongType, false),
            StructField("Stop", DataTypes.LongType, false)
        ))

        val conn_st = StructType(List(
            StructField("FlowUp", DataTypes.IntegerType, false),
            StructField("FlowDown", DataTypes.IntegerType, false),
            StructField("PktUp", DataTypes.IntegerType, false),
            StructField("PktDown", DataTypes.IntegerType, false),
            StructField("IpFragUp", DataTypes.IntegerType, false),
            StructField("IpFragDown", DataTypes.IntegerType, false)
        ))

        val tcp = StructType(List(
            StructField("DisorderUp", DataTypes.IntegerType),
            StructField("DisorderDown", DataTypes.IntegerType),
            StructField("RetranUp", DataTypes.IntegerType),
            StructField("RetranDown", DataTypes.IntegerType),
            StructField("SynAckDelay", DataTypes.IntegerType),
            StructField("AckDelay", DataTypes.IntegerType),
            StructField("ReportFlag", DataTypes.IntegerType),
            StructField("CloseReason", DataTypes.IntegerType),
            StructField("FirstRequestDelay", DataTypes.IntegerType),
            StructField("FirstResponseDely", DataTypes.IntegerType),
            StructField("Window", DataTypes.IntegerType),
            StructField("Mss", DataTypes.IntegerType),
            StructField("SynCount", DataTypes.IntegerType),
            StructField("SynAckCount", DataTypes.IntegerType),
            StructField("AckCount", DataTypes.IntegerType),
            StructField("SessionOK", DataTypes.BooleanType),
            StructField("Handshake12", DataTypes.BooleanType),
            StructField("Handshake23", DataTypes.BooleanType),
            StructField("Open", DataTypes.BooleanType),
            StructField("Close", DataTypes.BooleanType)
        ))

        val http = StructType(List(
            StructField("Host", DataTypes.StringType),
            StructField("Url", DataTypes.StringType),
            StructField("XonlineHost", DataTypes.StringType),
            StructField("UserAgent", DataTypes.StringType),
            StructField("ContentType", DataTypes.StringType),
            StructField("Refer", DataTypes.StringType),
            StructField("Cookie", DataTypes.StringType),
            StructField("Location", DataTypes.StringType),
            StructField("RequestLocation", bigfile),
            StructField("ResponseLocation", bigfile),
            StructField("RequestTime", DataTypes.IntegerType),
            StructField("FirstResponseTime", DataTypes.IntegerType),
            StructField("LastContentTime", DataTypes.IntegerType),
            StructField("ServTime", DataTypes.IntegerType),
            StructField("ContentLen", DataTypes.IntegerType),
            StructField("StateCode", DataTypes.IntegerType),
            StructField("Method", DataTypes.IntegerType),
            StructField("Version", DataTypes.IntegerType),
            StructField("HeadFlag", DataTypes.BooleanType),
            StructField("ServFlag", DataTypes.IntegerType),
            StructField("RequestFlag", DataTypes.BooleanType),
            StructField("Browser", DataTypes.IntegerType),
            StructField("Portal", DataTypes.IntegerType)
        ))

        val sip = StructType(List(
            StructField("CallingNo", DataTypes.StringType),
            StructField("CalledNo", DataTypes.StringType),
            StructField("SessionId", DataTypes.StringType),
            StructField("CallDir", DataTypes.IntegerType),
            StructField("CallType", DataTypes.IntegerType),
            StructField("HangupReason", DataTypes.IntegerType),
            StructField("StreamCount", DataTypes.IntegerType),
            StructField("Malloc", DataTypes.BooleanType),
            StructField("Bye", DataTypes.BooleanType),
            StructField("Invite", DataTypes.BooleanType)
        ))

        val rtsp = StructType(List(
            StructField("Url", DataTypes.StringType),
            StructField("UserAgent", DataTypes.StringType),
            StructField("ServerIp", DataTypes.StringType),
            StructField("ClientBeginPort", DataTypes.IntegerType),
            StructField("ClientEndPort", DataTypes.IntegerType),
            StructField("ServerBeginPort", DataTypes.IntegerType),
            StructField("ServerEndPort", DataTypes.IntegerType),
            StructField("VideoStreamCount", DataTypes.IntegerType),
            StructField("AudeoStreamCount", DataTypes.IntegerType),
            StructField("ResDelay", DataTypes.IntegerType)
        ))

        val ftp = StructType(List(
            StructField("State", DataTypes.IntegerType),
            StructField("User", DataTypes.StringType),
            StructField("CurrentDir", DataTypes.StringType),
            StructField("TransMode", DataTypes.IntegerType),
            StructField("TransType", DataTypes.IntegerType),
            StructField("FileCount", DataTypes.IntegerType),
            StructField("FileSize", DataTypes.IntegerType),
            StructField("RspTm", DataTypes.IntegerType),
            StructField("TransTm", DataTypes.IntegerType)
        ))

        val mail = StructType(List(
            StructField("MsgType", DataTypes.IntegerType),
            StructField("RspState", DataTypes.IntegerType),
            StructField("UserName", DataTypes.StringType),
            StructField("RecverInfo", DataTypes.StringType),
            StructField("Len", DataTypes.IntegerType),
            StructField("DomainInfo", DataTypes.StringType),
            StructField("RecvAccount", DataTypes.StringType),
            StructField("Hdr", DataTypes.StringType),
            StructField("AcsType", DataTypes.IntegerType)
        ))

        val dns_ips: ArrayType = DataTypes.createArrayType(DataTypes.StringType)

        val dns = StructType(List(
            StructField("Domain", DataTypes.StringType),
            StructField("IpCount", DataTypes.IntegerType),
            StructField("IpVersion", DataTypes.IntegerType),
            StructField("Ip", DataTypes.StringType),
            StructField("Ips", dns_ips),
            StructField("RspCode", DataTypes.IntegerType),
            StructField("ReqCount", DataTypes.IntegerType),
            StructField("RspRecordCount", DataTypes.IntegerType),
            StructField("AuthCnttCount", DataTypes.IntegerType),
            StructField("ExtraRecordCount", DataTypes.IntegerType),
            StructField("PktValid", DataTypes.BooleanType)
        ))

        val ssl = StructType(List(
            StructField("FailReason", DataTypes.IntegerType),
            StructField("Server", ssl_endpoint, false),
            StructField("Client", ssl_endpoint)
        ))

        val app = StructType(List(
            StructField("Status", DataTypes.StringType),
            StructField("File", DataTypes.StringType),
            StructField("FileLocation", bigfile)
        ))

        val root = StructType(List(
            StructField("Class", DataTypes.IntegerType, false),
            StructField("Ipv4", DataTypes.IntegerType, false),
            StructField("Time", DataTypes.LongType, false),
            StructField("Type", DataTypes.IntegerType, false),
            StructField("App", app),
            StructField("Conn", conn, false),
            StructField("ConnTm", conn_tm, false),
            StructField("ConnSt", conn_st, false),
            StructField("Tcp", tcp),
            StructField("Http", http),
            StructField("Dns", dns),
            StructField("Ssl", ssl),
            StructField("Ftp", ftp),
            StructField("Sip", sip),
            StructField("Mail", mail),
            StructField("Rtsp", rtsp)
        ))
    }

    def apply(jsonxdr: String): Option[Jxdr] = {
        import Jobject._

        try {
            val j = Jobject(jsonxdr)
            val x = new Jxdr()

            x.j2Root(j)
            x.j2App(jobject(j, "App"))
            x.j2Conn(jobject(j, "Conn"))
            x.j2ConnSt(jobject(j, "ConnSt"))
            x.j2ConnTm(jobject(j, "ConnTm"))

            if (const.conn.proto.tcp == x.conn.proto) {
                x.j2Tcp(j)
            }

            x.appid match {
                case const.appid.http => x.j2Http(jobject(j, "Http"))
                case const.appid.dns => x.j2Dns(jobject(j, "Dns"))
                case const.appid.ssl => x.j2Ssl(jobject(j, "Ssl"))
                case const.appid.ftp => x.j2Ftp(jobject(j, "Ftp"))
                case const.appid.sip => x.j2Sip(jobject(j, "Sip"))
                case const.appid.mail => x.j2Mail(jobject(j, "Mail"))
                case const.appid.rtsp => x.j2Rtsp(jobject(j, "Rtsp"))
            }

            Some(x)
        } catch {
            case _: Any => None
        }
    }

}
