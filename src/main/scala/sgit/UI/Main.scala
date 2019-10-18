package sgit.UI

import scopt.OParser
import sgit.commands.Init


object Main extends App {

  OParser.parse(Parser.parser, args, Config()) match {

    case Some(config) => {
      println(config.command)
      config.command match {
        case "init" => Init.Init()
        case "add" =>
        case "status" =>
        case "diff" =>
        case "commit" =>
        case _ => println("")

      }
    }

    case None => println("")
  }
}



