package io.github.emputi.mc.miniaturengine.example;

import io.github.emputi.mc.miniaturengine.application.Bootstrapper;
import io.github.emputi.mc.miniaturengine.command.CommandProcessor;
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElement;
import io.github.emputi.mc.miniaturengine.command.parameter.impl.ParameterElementAction;
import org.bukkit.Bukkit;
import java.util.Objects;

public class CommandJavaExample extends CommandProcessor
{
    public CommandJavaExample()
    {
        super("example", Objects.requireNonNull(Bootstrapper.Companion.getBootstrapperBase()));
        ParameterElement parameter = ParameterElement.CreateDelicatedParameterElement("arg1", null,
                new ParameterElementAction(eventArguments -> {
                    eventArguments.getClicker().sendMessage("Hello, This is test for java.");
                    return true; })
                , false);
        this.addParameterOfArgument(parameter);

        Bukkit.getConsoleSender().sendMessage(Objects.requireNonNull(parameter.getAction()).getFunctionId());
    }
}
