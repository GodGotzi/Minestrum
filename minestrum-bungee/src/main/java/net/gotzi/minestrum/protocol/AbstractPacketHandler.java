package net.gotzi.minestrum.protocol;

import net.gotzi.minestrum.protocol.packet.BossBar;
import net.gotzi.minestrum.protocol.packet.Chat;
import net.gotzi.minestrum.protocol.packet.ClearTitles;
import net.gotzi.minestrum.protocol.packet.ClientChat;
import net.gotzi.minestrum.protocol.packet.ClientCommand;
import net.gotzi.minestrum.protocol.packet.ClientSettings;
import net.gotzi.minestrum.protocol.packet.ClientStatus;
import net.gotzi.minestrum.protocol.packet.Commands;
import net.gotzi.minestrum.protocol.packet.EncryptionRequest;
import net.gotzi.minestrum.protocol.packet.EncryptionResponse;
import net.gotzi.minestrum.protocol.packet.EntityStatus;
import net.gotzi.minestrum.protocol.packet.GameState;
import net.gotzi.minestrum.protocol.packet.Handshake;
import net.gotzi.minestrum.protocol.packet.KeepAlive;
import net.gotzi.minestrum.protocol.packet.Kick;
import net.gotzi.minestrum.protocol.packet.LegacyHandshake;
import net.gotzi.minestrum.protocol.packet.LegacyPing;
import net.gotzi.minestrum.protocol.packet.Login;
import net.gotzi.minestrum.protocol.packet.LoginPayloadRequest;
import net.gotzi.minestrum.protocol.packet.LoginPayloadResponse;
import net.gotzi.minestrum.protocol.packet.LoginRequest;
import net.gotzi.minestrum.protocol.packet.LoginSuccess;
import net.gotzi.minestrum.protocol.packet.PingPacket;
import net.gotzi.minestrum.protocol.packet.PlayerChat;
import net.gotzi.minestrum.protocol.packet.PlayerListHeaderFooter;
import net.gotzi.minestrum.protocol.packet.PlayerListItem;
import net.gotzi.minestrum.protocol.packet.PluginMessage;
import net.gotzi.minestrum.protocol.packet.Respawn;
import net.gotzi.minestrum.protocol.packet.ScoreboardDisplay;
import net.gotzi.minestrum.protocol.packet.ScoreboardObjective;
import net.gotzi.minestrum.protocol.packet.ScoreboardScore;
import net.gotzi.minestrum.protocol.packet.SetCompression;
import net.gotzi.minestrum.protocol.packet.StatusRequest;
import net.gotzi.minestrum.protocol.packet.StatusResponse;
import net.gotzi.minestrum.protocol.packet.Subtitle;
import net.gotzi.minestrum.protocol.packet.SystemChat;
import net.gotzi.minestrum.protocol.packet.TabCompleteRequest;
import net.gotzi.minestrum.protocol.packet.TabCompleteResponse;
import net.gotzi.minestrum.protocol.packet.Team;
import net.gotzi.minestrum.protocol.packet.Title;
import net.gotzi.minestrum.protocol.packet.TitleTimes;
import net.gotzi.minestrum.protocol.packet.ViewDistance;

public abstract class AbstractPacketHandler
{

    public void handle(LegacyPing ping) throws Exception
    {
    }

    public void handle(TabCompleteResponse tabResponse) throws Exception
    {
    }

    public void handle(PingPacket ping) throws Exception
    {
    }

    public void handle(StatusRequest statusRequest) throws Exception
    {
    }

    public void handle(StatusResponse statusResponse) throws Exception
    {
    }

    public void handle(Handshake handshake) throws Exception
    {
    }

    public void handle(KeepAlive keepAlive) throws Exception
    {
    }

    public void handle(Login login) throws Exception
    {
    }

    public void handle(Chat chat) throws Exception
    {
    }

    public void handle(ClientChat chat) throws Exception
    {
    }

    public void handle(PlayerChat chat) throws Exception
    {
    }

    public void handle(SystemChat chat) throws Exception
    {
    }

    public void handle(ClientCommand command) throws Exception
    {
    }

    public void handle(Respawn respawn) throws Exception
    {
    }

    public void handle(LoginRequest loginRequest) throws Exception
    {
    }

    public void handle(ClientSettings settings) throws Exception
    {
    }

    public void handle(ClientStatus clientStatus) throws Exception
    {
    }

    public void handle(PlayerListItem playerListItem) throws Exception
    {
    }

    public void handle(PlayerListHeaderFooter playerListHeaderFooter) throws Exception
    {
    }

    public void handle(TabCompleteRequest tabComplete) throws Exception
    {
    }

    public void handle(ScoreboardObjective scoreboardObjective) throws Exception
    {
    }

    public void handle(ScoreboardScore scoreboardScore) throws Exception
    {
    }

    public void handle(EncryptionRequest encryptionRequest) throws Exception
    {
    }

    public void handle(ScoreboardDisplay displayScoreboard) throws Exception
    {
    }

    public void handle(Team team) throws Exception
    {
    }

    public void handle(Title title) throws Exception
    {
    }

    public void handle(Subtitle title) throws Exception
    {
    }

    public void handle(TitleTimes title) throws Exception
    {
    }

    public void handle(ClearTitles title) throws Exception
    {
    }

    public void handle(PluginMessage pluginMessage) throws Exception
    {
    }

    public void handle(Kick kick) throws Exception
    {
    }

    public void handle(EncryptionResponse encryptionResponse) throws Exception
    {
    }

    public void handle(LoginSuccess loginSuccess) throws Exception
    {
    }

    public void handle(LegacyHandshake legacyHandshake) throws Exception
    {
    }

    public void handle(SetCompression setCompression) throws Exception
    {
    }

    public void handle(BossBar bossBar) throws Exception
    {
    }

    public void handle(LoginPayloadRequest request) throws Exception
    {
    }

    public void handle(LoginPayloadResponse response) throws Exception
    {
    }

    public void handle(EntityStatus status) throws Exception
    {
    }

    public void handle(Commands commands) throws Exception
    {
    }

    public void handle(ViewDistance viewDistance) throws Exception
    {
    }

    public void handle(GameState gameState) throws Exception
    {
    }
}
