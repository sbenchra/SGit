package sgit.UI

import scopt.OParser
import sgit.commands.{Add, Branch, Commit, Diff, Init, Log, Status, Tag}


object Main extends App {

  OParser.parse(Parser.parser, args, Config()) match {

    case Some(config) => {
      println(config.command)
      config.command match {
        case "init" => Init.Init()
        case "add" =>Add.add(config.lFile)
        case "status" =>Status.status()
        case "diff" =>Diff.diff()
        case "commit" => Commit.commit(config.messageCommit)
        case "log -p" =>Log.logP()
        case "log -stat"=>Log.logStat()
        case "branch" =>Branch.branch(config.element)
        case "tag"=>Tag.createTag(config.element)
        case "branch -av"=>Branch.branchAv()
        case _ => println("Invalid command")

      }
    }

    case None => println("")
  }
}



