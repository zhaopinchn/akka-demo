package service

class FrontService {

  def justDoIt(): Unit = {
    println("""do something before""")
    FmEventBus.publish(FmEventType.HELLO, "ZhaoPin")
    println("""do something later""")
  }
}
