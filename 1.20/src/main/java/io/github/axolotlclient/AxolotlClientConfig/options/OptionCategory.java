package io.github.axolotlclient.AxolotlClientConfig.options;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.CategoryWidget;
import io.github.axolotlclient.AxolotlClientConfig.util.ConfigUtils;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.quiltmc.qsl.command.api.client.ClientCommandManager;
import org.quiltmc.qsl.command.api.client.ClientCommandRegistrationCallback;
import org.quiltmc.qsl.command.api.client.QuiltClientCommandSource;

public class OptionCategory extends io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory implements Tooltippable, WidgetSupplier, ConfigPart {

	public OptionCategory(String key) {
		super(key);
	}

	public OptionCategory(String key, boolean registerCommand) {
		super(key, registerCommand);
	}

	public LiteralArgumentBuilder<QuiltClientCommandSource> buildCommand() {
		LiteralArgumentBuilder<QuiltClientCommandSource> builder = ClientCommandManager.literal(getName());
		for (io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o : getOptions()) {
			((Option<?>) o).getCommand(builder);
		}
		builder.executes(context -> {
			StringBuilder string = new StringBuilder();
			for (io.github.axolotlclient.AxolotlClientConfig.common.options.Option<?> o : getOptions()) {
				string.append("    ").append(Formatting.AQUA).append(o.getName()).append(": ").append(o.get()).append("\n");
			}
			ConfigUtils.sendChatMessage(Text.literal(Formatting.BLUE + "Values in this category are: \n" + string));
			return Command.SINGLE_SUCCESS;
		});
		return builder;
	}

	@Override
	public ClickableWidget getWidget(int x, int y, int width, int height) {
		return new CategoryWidget(this, x, y, width, height);
	}

	@Override
	public void registerCommand() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext, environment) -> ClientCommandManager.getDispatcher().register(buildCommand()));
	}
}
