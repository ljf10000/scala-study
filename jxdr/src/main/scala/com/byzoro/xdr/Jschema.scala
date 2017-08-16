package com.byzoro.xdr

import org.apache.spark.sql.types._

object Jschema {
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
