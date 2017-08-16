package com.byzoro.xdr

import com.alibaba.fastjson.{JSONArray, JSONObject}
import com.byzoro.json.{Jobject, Jarray}

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
            Jxdr.TCP_COMPLETE == this.report_flag
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
        this.vender = Jobject.jstring(j, "Vendor")
        this.id = Jobject.jint(j, "Id")
        this.ipv4 = Jobject.jbool(j, "Ipv4")
        this.appid = Jobject.jint(j, "Class")
        this.Type = Jobject.jint(j, "Type")
        this.time = Jobject.jlong(j, "Time")
    }

    // not use json ConnEx/ServSt/Vpn/Proxy/QQ

    // not in json
    var ip_proto: uint8_t = Jxdr.TCP

    var vender: String = _
    var id: Int = _
    var ipv4: Boolean = true
    var appid: uint8_t = _
    var Type: Int = Jxdr.XDR_NORMAL
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

            if (TCP == x.conn.proto) {
                x.j2Tcp(j)
            }

            x.appid match {
                case HTTP => x.j2Http(jobject(j, "Http"))
                case DNS => x.j2Dns(jobject(j, "Dns"))
                case SSL => x.j2Ssl(jobject(j, "Ssl"))
                case FTP => x.j2Ftp(jobject(j, "Ftp"))
                case SIP => x.j2Sip(jobject(j, "Sip"))
                case MAIL => x.j2Mail(jobject(j, "Mail"))
                case RTSP => x.j2Rtsp(jobject(j, "Rtsp"))
            }

            Some(x)
        } catch {
            case _: Any => None
        }
    }
}
