package net.gotzi.bungee.protocol;

import lombok.Data;

@Data
public class PlayerPublicKey
{

    private final long expiry;
    private final byte[] key;
    private final byte[] signature;
}
