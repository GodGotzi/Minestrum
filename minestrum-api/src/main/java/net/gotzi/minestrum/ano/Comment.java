/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.ano;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface Comment {

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.CONSTRUCTOR)
    @interface Constructor {

    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface Getter {

    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    @interface Setter {

    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @interface Init {

    }
}

/*

Variablen (int zahl, int primzahlFlag, char check),

Titlel nicht in schleife + *****

do {
    variablen reset

    Zahl eingeben in schleife am Anfang

	Alogrithums für die Hauptausgabe unseres Programms

	Ausgabe ob Zahl Primzahl

	neue primzahlbestimmung an letzter stelle der schleife
} while(check if neuer Durchlauf);

*/
