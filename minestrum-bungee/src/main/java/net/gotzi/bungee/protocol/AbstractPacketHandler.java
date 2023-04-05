package net.gotzi.bungee.protocol;

import net.gotzi.bungee.protocol.packet.BossBar;
import net.gotzi.bungee.protocol.packet.Chat;
import net.gotzi.bungee.protocol.packet.ClearTitles;
import net.gotzi.bungee.protocol.packet.ClientChat;
import net.gotzi.bungee.protocol.packet.ClientCommand;
import net.gotzi.bungee.protocol.packet.ClientSettings;
import net.gotzi.bungee.protocol.packet.ClientStatus;
import net.gotzi.bungee.protocol.packet.Commands;
import net.gotzi.bungee.protocol.packet.EncryptionRequest;
import net.gotzi.bungee.protocol.packet.EncryptionResponse;
import net.gotzi.bungee.protocol.packet.EntityStatus;
import net.gotzi.bungee.protocol.packet.GameState;
import net.gotzi.bungee.protocol.packet.Handshake;
import net.gotzi.bungee.protocol.packet.KeepAlive;
import net.gotzi.bungee.protocol.packet.Kick;
import net.gotzi.bungee.protocol.packet.LegacyHandshake;
import net.gotzi.bungee.protocol.packet.LegacyPing;
import net.gotzi.bungee.protocol.packet.Login;
import net.gotzi.bungee.protocol.packet.LoginPayloadRequest;
import net.gotzi.bungee.protocol.packet.LoginPayloadResponse;
import net.gotzi.bungee.protocol.packet.LoginRequest;
import net.gotzi.bungee.protocol.packet.LoginSuccess;
import net.gotzi.bungee.protocol.packet.PingPacket;
import net.gotzi.bungee.protocol.packet.PlayerChat;
import net.gotzi.bungee.protocol.packet.PlayerListHeaderFooter;
import net.gotzi.bungee.protocol.packet.PlayerListItem;
import net.gotzi.bungee.protocol.packet.PluginMessage;
import net.gotzi.bungee.protocol.packet.Respawn;
import net.gotzi.bungee.protocol.packet.ScoreboardDisplay;
import net.gotzi.bungee.protocol.packet.ScoreboardObjective;
import net.gotzi.bungee.protocol.packet.ScoreboardScore;
import net.gotzi.bungee.protocol.packet.SetCompression;
import net.gotzi.bungee.protocol.packet.StatusRequest;
import net.gotzi.bungee.protocol.packet.StatusResponse;
import net.gotzi.bungee.protocol.packet.Subtitle;
import net.gotzi.bungee.protocol.packet.SystemChat;
import net.gotzi.bungee.protocol.packet.TabCompleteRequest;
import net.gotzi.bungee.protocol.packet.TabCompleteResponse;
import net.gotzi.bungee.protocol.packet.Team;
import net.gotzi.bungee.protocol.packet.Title;
import net.gotzi.bungee.protocol.packet.TitleTimes;
import net.gotzi.bungee.protocol.packet.ViewDistance;

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
