package imadz.team.efficiency.application.service

trait AppService[-Req, +Resp] {

  def execute(req: Req): Resp
}
