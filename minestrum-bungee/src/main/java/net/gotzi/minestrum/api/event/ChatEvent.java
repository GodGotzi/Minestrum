package net.gotzi.minestrum.api.event;

import net.gotzi.minestrum.api.CommandSender;
import net.gotzi.minestrum.api.ProxyServer;
import net.gotzi.minestrum.api.connection.Connection;
import net.gotzi.minestrum.api.plugin.Cancellable;
import net.gotzi.minestrum.api.plugin.PluginManager;

/**
 * Event called when a player sends a message to a server.
 */
public class ChatEvent extends TargetedEvent implements Cancellable
{

    /**
     * Cancelled state.
     */
    private boolean cancelled;
    /**
     * Text contained in this chat.
     */
    private String message;

    public ChatEvent(Connection sender, Connection receiver, String message)
    {
        super( sender, receiver );
        this.message = message;
    }

    /**
     * Checks whether this message is valid as a command
     *
     * @return if this message is a command
     */
    public boolean isCommand()
    {
        return message.length() > 0 && message.charAt( 0 ) == '/';
    }

    /**
     * Checks whether this message is run on this proxy server.
     *
     * @return if this command runs on the proxy
     * @see PluginManager#isExecutableCommand(java.lang.String,
     * CommandSender)
     */
    public boolean isProxyCommand()
    {
        if ( !isCommand() )
        {
            return false;
        }

        int index = message.indexOf( " " );
        String commandName = ( index == -1 ) ? message.substring( 1 ) : message.substring( 1, index );
        CommandSender sender = ( getSender() instanceof CommandSender ) ? (CommandSender) getSender() : null;

        return ProxyServer.getInstance().getPluginManager().isExecutableCommand( commandName, sender );
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public String getMessage() {
        return this.message;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChatEvent)) return false;
        final ChatEvent other = (ChatEvent) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (this.isCancelled() != other.isCancelled()) return false;
        final Object this$message = this.getMessage();
        final Object other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChatEvent;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + (this.isCancelled() ? 79 : 97);
        final Object $message = this.getMessage();
        result = result * PRIME + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "ChatEvent(super=" + super.toString() + ", cancelled=" + this.isCancelled() + ", message=" + this.getMessage() + ")";
    }
}
