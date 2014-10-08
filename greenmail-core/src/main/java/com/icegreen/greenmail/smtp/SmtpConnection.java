/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been used and modified. Original file can be found on http://foedus.sourceforge.net
 */
package com.icegreen.greenmail.smtp;

import com.icegreen.greenmail.util.InternetPrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class SmtpConnection {

    private static final int TIMEOUT_MILLIS = 1000 * 30;
    private InetAddress serverAddress;
    // Logger.
    protected final static Logger log = LoggerFactory.getLogger(SmtpConnection.class);

    {
        try {
            serverAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
            log.error("Could not get address of localhost", uhe);
        }
    }


    // networking/io stuff
    Socket sock;
    InetAddress clientAddress;
    InternetPrintWriter out;
    BufferedReader in;
    SmtpHandler handler;
    String heloName;

    public SmtpConnection(SmtpHandler handler, Socket sock)
            throws IOException {
        this.sock = sock;
        sock.setSoTimeout(TIMEOUT_MILLIS);
        clientAddress = sock.getInetAddress();
        OutputStream o = sock.getOutputStream();
        InputStream i = sock.getInputStream();
        out = new InternetPrintWriter(o, true);
        in = new BufferedReader(new InputStreamReader(i));

        this.handler = handler;
    }

    public void println(String line) {
        if (log.isDebugEnabled()) {
            log.debug("S: " + line);
        }
        out.println(line);
    }

    public BufferedReader getReader() {
        return in;
    }

    public String readLine()
            throws IOException {
        String line = in.readLine();
        if (log.isDebugEnabled()) {
            log.debug("C: " + line);
        }

        return line;
    }

    public String getClientAddress() {

        return clientAddress.getHostAddress();
    }

    public InetAddress getServerAddress() {

        return serverAddress;
    }

    public String getServerGreetingsName() {
        InetAddress serverAddress = getServerAddress();

        if (serverAddress != null)

            return serverAddress.toString();
        else

            return System.getProperty("user.name");
    }

    public String getHeloName() {

        return heloName;
    }

    public void setHeloName(String n) {
        heloName = n;
    }

    public void quit() {
        handler.quit();
    }
}