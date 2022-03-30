package io.github.oybek

import cats.implicits.catsSyntaxOptionId
import io.github.oybek.cstrike.model.Command.helpText
import io.github.oybek.fakes.FakeData.fakeChatId
import io.github.oybek.fakes.FakeData.fakeUser
import io.github.oybek.model.Reaction
import io.github.oybek.model.Reaction.SendText
import io.github.oybek.hub.Hub
import io.github.oybek.setup.HubSetup
import io.github.oybek.setup.TestEffect.F
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import telegramium.bots.Markdown

class SayCommandScenario extends AnyFeatureSpec with GivenWhenThen with HubSetup:

  info("As a user")
  info("I want to be able to send message to dedicated counter strike server")

  Feature("/say command") {
    Scenario("User writes command '/say' before '/new' command") {
      hldsConsole.reset
      Given("console without created server")
      When("/say command received")
      Then("message about server creation is returned")
      assert(hub.handle(fakeChatId, fakeUser, "/say hello") ===
        Right(List(SendText(fakeChatId, "Create a server first (/help)"))))
    }

    Scenario("User gives command '/say' after '/new' command") {
      hldsConsole.reset
      Given("console with created server")
      hub.handle(fakeChatId, fakeUser, "/new")

      When("/say command received")
      Then("message to dedicated server is sent")
      assert(hub.handle(fakeChatId, fakeUser, "/say hello") ===
        Right(List.empty[Reaction]))

      assert(
        hldsConsole.getCalledCommands === List(
          "sv_password 4444",
          "map de_dust2",
          "sv_password 4444",
          "changelevel de_dust2",
          "say hello"))
    }
  }
