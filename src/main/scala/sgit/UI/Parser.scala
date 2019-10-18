package sgit.UI

import scopt.OParser

case class Config(
                     command:String="",
                     path:String=".",
                     element :String="",
                     option :String="",
                     lFile:List[String]=List(),
                     messageCommit :String = "")

object Parser {
  val builder = OParser.builder[Config]
  val parser = {
    import builder._
    OParser.sequence(
      programName("sgit"),
      head("sgit"),
      help("help").text("The command of sgit are the following"),
      cmd("init")
        .action((_, c) => c.copy(command = "init"))
        .text("Initialize the repository"),
      cmd("status")
        .action((_, c) => c.copy(command = "status"))
        .text("Show the diffrence between the stage and the working directory"),
      cmd("diff")
        .action((_, c) => c.copy(command = "diff"))
        .text("Show changes between commit and its parent"),
      cmd("add")
        .action((_, c) => c.copy(command = "add"))
        .text("Add file a file or list of files to the stage")
        .children(
          arg[String]("<file-name.txt> ")
            .unbounded()
            .action((x, c) => c.copy(lFile = c.lFile :+ x))
            .text("List of files")
        ),
      cmd("commit")
        .action((_, c) => c.copy(command = "commit"))
        .text("Save the changes in the sgit repository").children(
        opt[String]('m', name = "message")
          .required()
          .maxOccurs(1)
          .action((x, c) => c.copy(messageCommit = x))
          .text("Commit message")
      ),
      cmd("log")
        .action((_, c) => c.copy(command = "log"))
        .text("Show commit logs")
        .children(
          opt[Unit]("p")
            .action((_, c) => c.copy(option = "p"))
            .text("Show diffrences and statistics of a commit and its parent"),
          opt[Unit]("stat")
            .action((_, c) => c.copy(option = "stat"))
            .text("Show statistics of a commit and its parent")
        ),

      cmd("branch")
        .action((_, c) => c.copy(command = "branch"))
        .text("Create a new branch")
        .children(
          arg[String]("<branch name>")
            .required()
            .action((x, c) => c.copy(element = x))
            .text("Branch name"),
          opt[Unit]("av")
            .action((_, c) => c.copy(option = "av"))
            .text("List all branches"),
        ),

      cmd("checkout")
        .action((_, c) => c.copy(command = "checkout"))
        .text("Switch to a branch")
        .children(
          arg[String]("<Branch or tag or commit hash>")
            .required()
            .action((x, c) => c.copy(element = x))
            .text(" ")
        ),

      cmd("tag")
        .action((_, c) => c.copy(command = "tag"))
        .text("Create a new tag")
        .children(
          arg[String]("<tag name>")
            .required()
            .action((x, c) => c.copy(element = x))
            .text("name of the tag we want to create")
        ),

    )
  }
}