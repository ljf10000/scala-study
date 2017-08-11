package jxdr

import com.alibaba.fastjson.{JSON => Json, JSONArray => Jarray, JSONObject => Jobject}
import org.apache.spark.sql.types._

class Jxdr {
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

    private def j2smallfile(j: Jobject): SmallFile = {
        if (null == j) {
            return null
        }

        SmallFile(dbname = j.getString("DbName"),
            tbname = j.getString("TableName"),
            signature = j.getString("Signature"))
    }

    case class BigFile(file: String, size: uint32_t, offset: offset_t, signature: String)

    private def j2bigfile(j: Jobject): BigFile = {
        if (null == j) {
            return null
        }

        BigFile(file = j.getString("File"),
            size = j.getIntValue("Size"),
            offset = j.getIntValue("Offset"),
            signature = j.getString("Signature"))
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

    private def j2cert(j: Jobject): Cert = {
        if (null == j) {
            return null
        }

        Cert(file = j2smallfile(j.getJSONObject("FileLocation")),
            version = j.getIntValue("Version"),
            serial_number = j.getString("SerialNumber"),
            key_usage = j.getIntValue("KeyUsage"),
            not_before = j.getLongValue("NotBefore"),
            not_after = j.getLongValue("NotAfter"),
            country_name = j.getString("CountryName"),
            organization_name = j.getString("OrganizationName"),
            organization_unit_name = j.getString("OrganizationUnitName"),
            common_name = j.getString("CommonName"))
    }

    private def j2certs(a: Jarray): Array[Cert] = {
        if (null == a) {
            return null
        }

        val count = a.size()
        val certs = new Array[Cert](count)

        for (i <- 0 until count) {
            certs(i) = j2cert(a.getJSONObject(i))
        }

        certs
    }

    case class SslEndpoint(verfy: Boolean,
                           verfy_failed_desc: String,
                           verfy_failed_idx: uint8_t,
                           cert: Cert,
                           certs: Array[Cert])

    private def j2ssl_endpoint(j: Jobject): SslEndpoint = {
        if (null == j) {
            return null
        }

        SslEndpoint(
            verfy = j.getBooleanValue("Verfy"),
            verfy_failed_desc = j.getString("VerfyFailedDesc"),
            verfy_failed_idx = j.getIntValue("VerfyFailedIdx"),
            cert = j2cert(j.getJSONObject("Cert")),
            certs = j2certs(j.getJSONArray("Certs")))
    }

    case class Conn(proto: uint8_t,
                    sport: uint16_t,
                    dport: uint16_t,
                    sip: String,
                    dip: String)

    var conn: Conn = _

    def j2Conn(j: Jobject): Unit = {
        if (null != j) {
            val proto = j.getIntValue("Proto")

            this.conn = Conn(
                proto = proto,
                sport = j.getIntValue("Sport"),
                dport = j.getIntValue("Dport"),
                sip = j.getString("Sip"),
                dip = j.getString("Dip"))
            this.ip_proto = proto
        }
    }

    case class ConnTm(start: time_t, stop: time_t)

    var conn_tm: ConnTm = _

    def j2ConnTm(j: Jobject): Unit = {
        if (null != j) {
            this.conn_tm = ConnTm(
                start = j.getLongValue("Start"),
                stop = j.getLongValue("End"))
        }
    }

    case class ConnSt(flow_up: uint32_t,
                      flow_down: uint32_t,
                      ip_packet_up: uint32_t,
                      ip_packet_down: uint32_t,
                      ip_frag_up: uint32_t,
                      ip_frag_down: uint32_t)

    var conn_st: ConnSt = _

    def j2ConnSt(j: Jobject): Unit = {
        if (null != j) {
            this.conn_st = ConnSt(
                flow_up = j.getIntValue("FlowUp"),
                flow_down = j.getIntValue("FlowDown"),
                ip_packet_up = j.getIntValue("PktUp"),
                ip_packet_down = j.getIntValue("PktDown"),
                ip_frag_up = j.getIntValue("IpFragUp"),
                ip_frag_down = j.getIntValue("IpFragDown"))
        }
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
            Jxdr.TCP_COMPLETE == this.report_flag
        }
    }

    var tcp: Tcp = _

    def j2Tcp(j: Jobject): Unit = {
        if (null != j) {
            this.tcp = Tcp(
                disorder_up = j.getIntValue("DisorderUp"),
                disorder_down = j.getIntValue("DisorderDown"),
                retran_up = j.getIntValue("RetranUp"),
                retran_down = j.getIntValue("RetranDown"),
                synack_to_syn_time = j.getIntValue("SynAckDelay"),
                ack_to_syn_time = j.getIntValue("AckDelay"),
                report_flag = j.getIntValue("ReportFlag"),
                close_reason = j.getIntValue("CloseReason"),
                first_request_delay = j.getIntValue("FirstRequestDelay"),
                first_response_delay = j.getIntValue("FirstResponseDely"),
                window = j.getIntValue("Window"),
                mss = j.getIntValue("Mss"),
                count_retry = j.getIntValue("SynCount"),
                count_retry_ack = j.getIntValue("SynAckCount"),
                count_ack = j.getIntValue("AckCount"),
                connect_success = j.getBooleanValue("SessionOK"),
                handshake12_success = j.getBooleanValue("Handshake12"),
                handshake23_success = j.getBooleanValue("Handshake23"),
                open_status = j.getIntValue("Open"),
                close_status = j.getIntValue("Close"))
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
                    file_request: BigFile,
                    file_response: BigFile,
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

    var http: Http = _

    def j2Http(j: Jobject): Unit = {
        if (null != j) {
            this.http = Http(
                host = j.getString("Host"),
                url = j.getString("Url"),
                host_xonline = j.getString("XonlineHost"),
                user_agent = j.getString("UserAgent"),
                content_type = j.getString("ContentType"),
                refer = j.getString("Refer"),
                cookie = j.getString("Cookie"),
                location = j.getString("Location"),
                file_request = j2bigfile(j.getJSONObject("RequestLocation")),
                file_response = j2bigfile(j.getJSONObject("ResponseLocation")),
                time_request = j.getLongValue("RequestTime"),
                time_first_response = j.getLongValue("FirstResponseTime"),
                time_last_content = j.getLongValue("LastContentTime"),
                service_delay = j.getLongValue("ServTime"),
                content_length = j.getIntValue("ContentLen"),
                status_code = j.getIntValue("StateCode"),
                method = j.getIntValue("Method"),
                version = j.getIntValue("Version"),
                head_flag = j.getBooleanValue("HeadFlag"),
                serv_flag = j.getIntValue("ServFlag"),
                first_request = j.getBooleanValue("RequestFlag"),
                brower = j.getIntValue("Browser"),
                portal = j.getIntValue("Portal"))
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

    var sip: Sip = _

    def j2Sip(j: Jobject): Unit = {
        if (null != j) {
            this.sip = Sip(
                calling_number = j.getString("CallingNo"),
                called_number = j.getString("CalledNo"),
                session_id = j.getString("SessionId"),
                call_direction = j.getIntValue("CallDir"),
                call_type = j.getIntValue("CallType"),
                hangup_reason = j.getIntValue("HangupReason"),
                signal_type = j.getIntValue("SignalType"),
                stream_count = j.getIntValue("StreamCount"),
                malloc = j.getBooleanValue("Malloc"),
                bye = j.getBooleanValue("Bye"),
                invite = j.getBooleanValue("Invite"))
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

    var rtsp: Rtsp = _

    def j2Rtsp(j: Jobject): Unit = {
        if (null != j) {
            this.rtsp = Rtsp(
                url = j.getString("Url"),
                user_agent = j.getString("UserAgent"),
                server_ip = j.getString("ServerIp"),
                port_client_start = j.getIntValue("ClientBeginPort"),
                port_client_end = j.getIntValue("ClientEndPort"),
                port_server_start = j.getIntValue("ServerBeginPort"),
                port_server_end = j.getIntValue("ServerEndPort"),
                count_video = j.getIntValue("VideoStreamCount"),
                count_audio = j.getIntValue("AudeoStreamCount"),
                describe_delay = j.getIntValue("ResDelay"))
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

    var ftp: Ftp = _

    def j2Ftp(j: Jobject): Unit = {
        if (null != j) {
            this.ftp = Ftp(
                state = j.getIntValue("State"),
                user = j.getString("User"),
                pwd = j.getString("CurrentDir"),
                trans_mode = j.getIntValue("TransMode"),
                trans_type = j.getIntValue("TransType"),
                filesize = j.getIntValue("FileSize"),
                response_delay = j.getLongValue("RspTm"),
                trans_duration = j.getLongValue("TransTm"))
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

    var mail: Mail = _

    def j2Mail(j: Jobject): Unit = {
        if (null != j) {
            this.mail = Mail(
                msg_type = j.getIntValue("MsgType"),
                status_code = j.getIntValue("RspState"),
                length = j.getIntValue("Len"),
                acs_type = j.getIntValue("AcsType"),
                user = j.getString("UserName"),
                domain = j.getString("DomainInfo"),
                sender = j.getString("RecverInfo"),
                recver = j.getString("RecvAccount"),
                hdr = j.getString("Hdr"))
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

    var dns: Dns = _

    def j2dns_ips(a: Jarray): Array[String] = {
        if (null == a) {
            return null
        }

        val count = a.size()
        val ips = new Array[String](count)

        for (i <- 0 until count) {
            ips(i) = a.getString(i)
        }

        ips
    }

    def j2Dns(j: Jobject): Unit = {
        if (null != j) {
            this.dns = Dns(
                response_code = j.getIntValue("RspCode"),
                count_request = j.getIntValue("ReqCount"),
                count_response_record = j.getIntValue("RspRecordCount"),
                count_response_auth = j.getIntValue("AuthCnttCount"),
                count_response_extra = j.getIntValue("ExtraRecordCount"),
                ip_version = j.getIntValue("IpVersion"),
                ip_count = j.getIntValue("IpCount"),
                delay = j.getIntValue("HeadFlag"),
                domain = j.getString("Domain"),
                ip = j.getString("Ip"),
                ips = j2dns_ips(j.getJSONArray("Ips")))
        }
    }

    case class Ssl(reason: uint8_t,
                   server: SslEndpoint,
                   client: SslEndpoint)

    var ssl: Ssl = _

    def j2Ssl(j: Jobject): Unit = {
        if (null != j) {
            this.ssl = Ssl(
                reason = j.getIntValue("FailReason"),
                server = j2ssl_endpoint(j.getJSONObject("Server")),
                client = j2ssl_endpoint(j.getJSONObject("Client")))
        }
    }

    // not use json ProtoInfo/ClassId/Proto
    case class App(status: uint8_t, local: String, file: BigFile)

    var app: App = _

    def j2App(j: Jobject): Unit = {
        if (null != j) {
            this.app = App(
                status = j.getIntValue("Status"),
                local = j.getString("File"),
                file = j2bigfile(j.getJSONObject("FileLocation")))
        }
    }

    var vender: String = _
    var id: Int = _
    var ipv4: Boolean = true
    var appid: uint8_t = _
    var Type: Int = Jxdr.XDR_NORMAL
    var time: time_t = _

    // not in json
    var ip_proto: uint8_t = Jxdr.TCP

    def j2Base(j: Jobject): Unit = {
        this.vender = j.getString("Vendor")
        this.id = j.getIntValue("Id")
        this.ipv4 = j.getBooleanValue("Ipv4")
        this.appid = j.getIntValue("Class")
        this.Type = j.getIntValue("Type")
        this.time = j.getLongValue("Time")
    }

    // not use json ConnEx/ServSt/Vpn/Proxy/QQ
}

object Jxdr {
    val TCP_COMPLETE = 1

    val TCP_OPEN_SUCCESS = 0
    val TCP_OPEN_SYN = 1
    val TCP_OPEN_SYNACK = 2

    val TCP_CLOSE_FIN = 0
    val TCP_CLOSE_CLIENT_RST = 1
    val TCP_CLOSE_SERVER_RST = 2
    val TCP_CLOSE_TIMEOUT = 3

    // type
    val XDR_NORMAL = 0
    val XDR_FILE = 1
    val XDR_HTTP = 2
    val XDR_CERT = 3

    // conn.proto
    val ICMP = 1
    val TCP = 6
    val UDP = 17

    // appid
    val COMMON = 100
    val DNS = 101
    val MMS = 102
    val HTTP = 103
    val FTP = 104
    val MAIL = 105
    val SIP = 106
    val RTSP = 107
    val P2P = 108
    val VIDEO = 109
    val IM = 110
    val SSL = 111

    // app.status
    val APP_SUCCESS = 0
    val APP_SYN = 1
    val APP_SYN_ACK = 2
    val APP_TIMEOUT = 3
    val APP_SERVER_REJECT = 4
    val APP_CLIENT_REJECT = 5
    val APP_SERVER_RST = 6
    val APP_CLIENT_RST = 7

    // http.version
    /*
    val HTTP_09 = 1
    val HTTP_10 = 2
    val HTTP_11 = 3
    val HTTP_20 = 4
    val WAP_10 = 5
    val WAP_11 = 6
    val WAP_12 = 7
    */

    // http.service
    /*
    val HTTP_SERVICE_SUCCESS = 0
    val HTTP_SERVICE_FAIL = 1
    val HTTP_SERVICE_UNKNOW = 2
    val HTTP_SERVICE_COMPLETE = 3
    */

    // http.portal
    /*
    val PORTAL_TENCENT = 1
    val PORTAL_TAOBAO = 2
    val PORTAL_SINA = 3
    val PORTAL_163 = 4
    val PORTAL_MOBILE = 5
    val PORTAL_SOHU = 6
    val PORTAL_IFENG = 7
    val PORTAL_BAIDU = 8
    */

    // http.browser
    /*
    val BROWSER_UC = 1
    val BROWSER_QQ = 2
    val BROWSER_360 = 3
    val BROWSER_SKYFIRE = 4
    val BROWSER_UBUNTU = 5
    val BROWSER_BAIDU = 6
    val BROWSER_CHROME = 7
    val BROWSER_FFIREFOX = 8
    val BROWSER_OPERA = 9
    val BROWSER_SAFARI = 10
    */

    val BIGFILE_SCHEMA = StructType(List(
        StructField("File", DataTypes.StringType, false),
        StructField("Size", DataTypes.IntegerType, false),
        StructField("Offset", DataTypes.IntegerType, false),
        StructField("Signature", DataTypes.StringType, false)
    ))

    val SMALLFILE_SCHEMA = StructType(List(
        StructField("DbName", DataTypes.StringType, false),
        StructField("TableName", DataTypes.StringType, false),
        StructField("Signature", DataTypes.StringType, false)
    ))

    val CERT_SCHEMA = StructType(List(
        StructField("FileLocation", SMALLFILE_SCHEMA, false),

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

    val CERTS_SCHEMA: ArrayType = DataTypes.createArrayType(CERT_SCHEMA, false)

    val SSL_ENDPOINT_SCHEMA = StructType(List(
        StructField("Verfy", DataTypes.BooleanType, false),
        StructField("VerfyFailedDesc", DataTypes.StringType),
        StructField("VerfyFailedIdx", DataTypes.IntegerType),
        StructField("Cert", CERT_SCHEMA, false),
        StructField("Certs", CERTS_SCHEMA, false)
    ))

    val CONN_SCHEMA = StructType(List(
        StructField("Proto", DataTypes.IntegerType, false),
        StructField("Sport", DataTypes.IntegerType, false),
        StructField("Dport", DataTypes.IntegerType, false),
        StructField("Sip", DataTypes.StringType, false),
        StructField("Dip", DataTypes.StringType, false)
    ))

    val CONN_TM_SCHEMA = StructType(List(
        StructField("Start", DataTypes.LongType, false),
        StructField("Stop", DataTypes.LongType, false)
    ))

    val CONN_ST_SCHEMA = StructType(List(
        StructField("FlowUp", DataTypes.IntegerType, false),
        StructField("FlowDown", DataTypes.IntegerType, false),
        StructField("PktUp", DataTypes.IntegerType, false),
        StructField("PktDown", DataTypes.IntegerType, false),
        StructField("IpFragUp", DataTypes.IntegerType, false),
        StructField("IpFragDown", DataTypes.IntegerType, false)
    ))

    val TCP_SCHEMA = StructType(List(
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

    val HTTP_SCHEMA = StructType(List(
        StructField("Host", DataTypes.StringType),
        StructField("Url", DataTypes.StringType),
        StructField("XonlineHost", DataTypes.StringType),
        StructField("UserAgent", DataTypes.StringType),
        StructField("ContentType", DataTypes.StringType),
        StructField("Refer", DataTypes.StringType),
        StructField("Cookie", DataTypes.StringType),
        StructField("Location", DataTypes.StringType),
        StructField("RequestLocation", BIGFILE_SCHEMA),
        StructField("ResponseLocation", BIGFILE_SCHEMA),
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

    val SIP_SCHEMA = StructType(List(
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

    val RTSP_SCHEMA = StructType(List(
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

    val FTP_SCHEMA = StructType(List(
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

    val MAIL_SCHEMA = StructType(List(
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

    val DNS_IPS_SCHEMA: ArrayType = DataTypes.createArrayType(DataTypes.StringType)

    val DNS_SCHEMA = StructType(List(
        StructField("Domain", DataTypes.StringType),
        StructField("IpCount", DataTypes.IntegerType),
        StructField("IpVersion", DataTypes.IntegerType),
        StructField("Ip", DataTypes.StringType),
        StructField("Ips", DNS_IPS_SCHEMA),
        StructField("RspCode", DataTypes.IntegerType),
        StructField("ReqCount", DataTypes.IntegerType),
        StructField("RspRecordCount", DataTypes.IntegerType),
        StructField("AuthCnttCount", DataTypes.IntegerType),
        StructField("ExtraRecordCount", DataTypes.IntegerType),
        StructField("PktValid", DataTypes.BooleanType)
    ))

    val SSL_SCHEMA = StructType(List(
        StructField("FailReason", DataTypes.IntegerType),
        StructField("Server", SSL_ENDPOINT_SCHEMA, false),
        StructField("Client", SSL_ENDPOINT_SCHEMA)
    ))

    val APP_SCHEMA = StructType(List(
        StructField("Status", DataTypes.StringType),
        StructField("File", DataTypes.StringType),
        StructField("FileLocation", BIGFILE_SCHEMA)
    ))

    val SCHEMA = StructType(List(
        StructField("Class", DataTypes.IntegerType, false),
        StructField("Ipv4", DataTypes.IntegerType, false),
        StructField("Time", DataTypes.LongType, false),
        StructField("Type", DataTypes.IntegerType, false),
        StructField("App", APP_SCHEMA),
        StructField("Conn", CONN_SCHEMA, false),
        StructField("ConnTm", CONN_TM_SCHEMA, false),
        StructField("ConnSt", CONN_ST_SCHEMA, false),
        StructField("Tcp", TCP_SCHEMA),
        StructField("Http", HTTP_SCHEMA),
        StructField("Dns", DNS_SCHEMA),
        StructField("Ssl", SSL_SCHEMA),
        StructField("Ftp", FTP_SCHEMA),
        StructField("Sip", SIP_SCHEMA),
        StructField("Mail", MAIL_SCHEMA),
        StructField("Rtsp", RTSP_SCHEMA)
    ))

    def apply(jsonxdr: String): Jxdr = {
        val j = Json.parseObject(jsonxdr)
        if (null == j) {
            return null
        }

        val x = new Jxdr()

        x.j2App(j)
        x.j2Base(j)
        x.j2Conn(j)
        x.j2ConnSt(j)
        x.j2ConnTm(j)

        if (TCP == x.conn.proto) {
            x.j2Tcp(j)
        }

        x.appid match {
            case HTTP => x.j2Http(j)
            case DNS => x.j2Dns(j)
            case SSL => x.j2Ssl(j)
            case FTP => x.j2Ftp(j)
            case SIP => x.j2Sip(j)
            case MAIL => x.j2Mail(j)
            case RTSP => x.j2Rtsp(j)
        }

        x
    }
}
