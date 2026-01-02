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

package ua.nanit.limbo.protocol.packets.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import ua.nanit.limbo.protocol.ByteMessage;
import ua.nanit.limbo.protocol.PacketOut;
import ua.nanit.limbo.protocol.registry.Version;
import ua.nanit.limbo.util.NbtMessageUtil;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PacketDisconnect implements PacketOut {

    private Component componentReason;
    private String legacyReason;

    @Override
    public void encode(@NonNull ByteMessage msg, @NonNull Version version) {
        if (legacyReason != null) {
            msg.writeString(String.format("{\"text\": \"%s\"}", legacyReason));
        } else if (componentReason != null) {
            msg.writeString(GsonComponentSerializer.gson().serialize(componentReason));
//            msg.writeNbtMessage(NbtMessageUtil.create(componentReason), version);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
