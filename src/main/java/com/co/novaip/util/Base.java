/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.co.novaip.util;

import java.io.Serializable;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;


public class Base implements Serializable {

    private static final long serialVersionUID = 1L;

    public Base() {
    }

    public static String getTexto(String locale, String llave) {

        try {
            String dir = "/opt/novaip/";

            Properties objProperties = new Properties();
            FileInputStream fiArchivoConfiguracion;
            fiArchivoConfiguracion = new FileInputStream(dir + locale + ".properties");
            String txt;
            try (InputStreamReader isr = new InputStreamReader(
              fiArchivoConfiguracion, "UTF8")) {
                objProperties.load(isr);
                txt = objProperties.getProperty(llave);
                fiArchivoConfiguracion.close();
            }
            return txt;

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getPropiedad(String locale, String llave) {
        String string = null;
        try {
            string = getTexto(locale, llave);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return string;
    }

    public static Double formatearValorDecimal(Double doub) {
        Locale loc = new Locale("en");
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(getPropiedadConfiguracion("formato_valor_decimal"));
        String output = df.format(doub);
        return Double.parseDouble(output);

    }

    public static String getPropiedadConfiguracion(String llave) {
        String string = null;
        try {
            string = getTexto("configuracion", llave);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return string;
    }

    public static String getPropiedadPbxWeb(String llave) {
        String string = null;
        try {
            string = getTexto("pbx_web", llave);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return string;
    }

    public static String formatearCerosIzquierda(Integer value) {
        return String.format(getPropiedadConfiguracion("formato_ceros_izquierda_codigo_de_plan"), value);
    }

}
