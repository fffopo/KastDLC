package kastdlc.client;

import kastdlc.client.gui.ClickGuiScreen;
import kastdlc.client.module.Module;
import kastdlc.client.module.ModuleManager;
import kastdlc.client.module.impl.render.ESP;
import kastdlc.client.module.impl.render.Fullbright;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KastDLCClient implements ClientModInitializer {

	public static final ModuleManager MODULE_MANAGER =
			new ModuleManager();

	private static KeyMapping clickGuiKey;

	private final Set<Integer> pressedKeys =
			new HashSet<>();

	@Override
	public void onInitializeClient() {

		MODULE_MANAGER.register(
				new Fullbright()
		);

		MODULE_MANAGER.register(
				new ESP()
		);

		clickGuiKey =
				new KeyMapping(
						"key.kastdlc.clickgui",
						GLFW.GLFW_KEY_RIGHT_SHIFT,
						KeyMapping.Category.MISC
				);

		ClientTickEvents.END_CLIENT_TICK.register(
				this::onClientTick
		);
	}

	private void onClientTick(
			Minecraft client
	) {

		while (clickGuiKey.consumeClick()) {

			if (client.screen == null) {

				client.setScreen(
						new ClickGuiScreen()
				);
			}
		}

		if (client.getWindow() == null) {
			return;
		}

		long window =
				client.getWindow()
						.handle();

		for (Module module :
				MODULE_MANAGER.getModules()) {

			if (!module.hasKey()) {
				continue;
			}

			int key =
					module.getKey();

			if (key < 0) {
				continue;
			}

			int state =
					GLFW.glfwGetKey(
							window,
							key
					);

			boolean down =
					state == GLFW.GLFW_PRESS;

			if (down) {

				if (pressedKeys.add(key)) {
					module.toggle();
				}

			} else {

				pressedKeys.remove(key);
			}
		}
	}
}