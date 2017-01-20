package com.phasmid.hedge_fund.http

import spray.http.Uri._

import spray.http.Uri
import scala.language.postfixOps

/**
 * @author robinhillyard
 */
class UriGet {
  def get(host: String, path: String, queryParams: Map[String, String], scheme: String = "https") =
    Uri(scheme, Authority(NamedHost(host)), Path(path)) withQuery (queryParams)
}