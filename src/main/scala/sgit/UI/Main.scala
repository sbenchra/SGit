package sgit.UI

import scopt.OParser
import sgit.commands._

object Main extends App {

  OParser.parse(Parser.parser, args, Config()) match {

    case Some(config) => {
      println(config.command)
      config.command match {
        case "init"       => Init.Init()
        case "add"        => Add.add(config.lFile)
        case "status"     => Status.status()
        case "diff"       => Diff.diff()
        case "commit"     => Commit.commit(config.messageCommit)
        case "log "       => Log.logP()
        case "log "       => Log.logStat()
        case "branch"     => Branch.branch(config.element)
        case "tag"        => Tag.createTag(config.element)
        case "branch -av" => Branch.branchAv()
        case _            => println("Invalid command")

      }
    }

    case None => println("")
  }
}
