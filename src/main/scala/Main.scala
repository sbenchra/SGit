import scopt.OParser
import sgit.commands.Tag
import sgit.UI.{Config, Parser}
import sgit.commands._

object Main extends App {

  OParser.parse(Parser.parser, args, Config()) match {

    case Some(config) => {
      println(config.command)
      config.command match {
        case "init"   => Init.Init()
        case "add"    => Add.add(config.lFile)
        case "status" => Status.status()
        case "diff"   => Diff.diff()
        case "commit" => Commit.commit(config.messageCommit)
        case "log" =>
          config match {
            case _ if config.p    => Log.logP()
            case _ if config.stat => Log.logStat()
            case _                => Log.log()
          }
        case "tag" => Tag.createTag(config.element)
        case "branch" =>
          if (config.av) Branch.branchAv()
          else Branch.branch(config.element)
        case "checkout" => Checkout.checkout(config.element)
        case _          => println("Invalid command")

      }
    }
    case None => println("")
  }
}
