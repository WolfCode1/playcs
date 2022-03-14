package io.github.oybek

import io.github.oybek.common.WithMeta
import io.github.oybek.common.withMeta
import io.github.oybek.exception.BusinessException.NoFreeConsolesException
import io.github.oybek.fakes.FakeData.{anotherFakeChatId, fakeChatId, fakePassword}
import io.github.oybek.model.Reaction.{SendText, Sleep}
import io.github.oybek.model.{ConsoleMeta, ConsolePool}
import io.github.oybek.setup.ConsoleSetup
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import telegramium.bots.Markdown

import java.time.Instant
import scala.concurrent.duration.DurationInt

class NewCommandSpec extends AnyFeatureSpec with GivenWhenThen with ConsoleSetup:

  info("As a user")
  info("I want to be able to create dedicated counter strike server")

  Feature("/new command") {
    Scenario("User gives command '/new'") {
      Given("console which has a free dedicated servers")

      When("/new command received")
      val result = console.handle(fakeChatId, "/new")

      Then("new dedicated server should be created")
      assert(
        hldsConsole.getCalledCommands ===
          List(
            s"sv_password $fakePassword",
            "map de_dust2",
            "changelevel de_dust2")
      )
      assert(consolePoolRef.get ===
        Right(ConsolePool(
          Nil,
          List(hldsConsole withMeta
            ConsoleMeta(
              "4444",
              fakeChatId,
              Instant.ofEpochSecond(15*60))))))

      Then("the instructions should be reported")
      assert(result === Right(List(
        SendText(fakeChatId, "Your server is ready. Copy paste this"),
        Sleep(200.millis),
        SendText(fakeChatId, "`connect 127.0.0.1:27015; password 4444`",Some(Markdown)))))
    }

    Scenario("User gives command '/new' when there is no free server") {
      Given("console which has no free dedicated servers")

      When("/new command received")
      val result = console.handle(anotherFakeChatId, "/new")

      Then("No servers left message should be returned")
      assert(result === Left(
        NoFreeConsolesException(List(SendText(anotherFakeChatId, "Can't create new server, connect @wolfodav")))
      ))
    }
  }
