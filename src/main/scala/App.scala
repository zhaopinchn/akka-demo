package example

import service.FrontService

object App {

  val frontService = new FrontService

  def main(args: Array[String]) {
    frontService.justDoIt()
  }
}

