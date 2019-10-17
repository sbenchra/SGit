package sgit.UI

import scopt.OParser
case class Config(
                   path:String=".",
                    mode :String="",
                    lFile:List[String]=List(),
                   messageCommit :String = "")


object Parser {

  val builder = OParser.builder[Config]
  val parser1 = {
    import builder._
    OParser.sequence(
      programName("sgit"),
      head("sgit", "1.0"),
      cmd("init")
        .action((_, c) => c.copy(mode = "init"))
        .text("Create an empty Git repository or reinitialize an existing one.")
    ) .children(
      arg[String]("<path>")
        .optional()
        .action((x, c) => c.copy(path = x))
    )
    cmd("add")
      .action((_, c) => c.copy(mode = "add"))
      .text("add file contents to the index")
      .children(
        arg[String]("<file>...")
          .unbounded()
          .required()
          .action((f, c) => c.copy(lFile = c.lFile :+ f))
          .text("file to add")
      )
    cmd(name = "commit")
      .action((_, c) => c.copy(mode = "commit"))
      .text("Record changes to the repository")
      .children(
        opt[String]('m', name = "message")
          .required()
          .action((x, c) => c.copy(messageCommit = x))
          .text("commit message")
      )

    cmd(name = "diff")
      .action((_, c) => c.copy(mode = "diff"))
      .text("Show changes between commits, commit and working tree, etc")
  }
/*
  // OParser.parse returns Option[Config]
  OParser.parse(parser1, args, Config()) match {
    case Some(config) =>
    // do something
    case _ =>
    // arguments are bad, error message will have been displayed
  }

 */
}
