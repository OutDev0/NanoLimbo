/*
 * Copyright (C) 2020 Nan1t
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ua.nanit.limbo.server.data;

import lombok.Data;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ua.nanit.limbo.util.ComponentUtils;

import java.lang.reflect.Type;

@Data
public class Title {

    private Component title;
    private Component subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public static class Serializer implements TypeSerializer<Title> {
        @Override
        public Title deserialize(Type type, ConfigurationNode node) {
            Title title = new Title();
            title.setTitle(ComponentUtils.parse(node.node("title").getString("")));
            title.setSubtitle(ComponentUtils.parse(node.node("subtitle").getString("")));
            title.setFadeIn(node.node("fadeIn").getInt(10));
            title.setStay(node.node("stay").getInt(100));
            title.setFadeOut(node.node("fadeOut").getInt(10));
            return title;
        }

        @Override
        public void serialize(Type type, @Nullable Title obj, ConfigurationNode node) {}
    }
}
