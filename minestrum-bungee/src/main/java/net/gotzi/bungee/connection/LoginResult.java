package net.gotzi.bungee.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.gotzi.bungee.protocol.Property;

@Data
@AllArgsConstructor
public class LoginResult
{

    private String id;
    private String name;
    private Property[] properties;
}
