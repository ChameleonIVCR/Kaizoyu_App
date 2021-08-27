package com.chame.kaizoyu.download.scrappers;


import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizolib.irc.exception.NoQuickRetryException;
import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizoyu.MainActivity;

import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class IRCGet implements Runnable{
    private final String command;
    private IRCListener listener;

    public IRCGet(String command){
        this.command = command;
    }

    public void setListener(IRCListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        final String username = MainActivity.getInstance().getConfigManager().getProperty("ircName");
        IrcClient irc = new IrcClient(command, username);
        try {
            DCC dcc = irc.execute();
            if (listener != null) listener.onIRCFinished(dcc);
            //File download = new File(path, dcc.getFilename());
            //download.createNewFile();
            //DCCDownloader dccDl = new DCCDownloader(dcc, download);
            //dHandler.setDownload(dccDl);
            //dccDl.start();
        } catch (UnknownHostException | NoQuickRetryException | TimeoutException e) {
            e.printStackTrace();
            if (listener != null) listener.onIRCFailed(e);
        }
    }
    public interface IRCListener{
        void onIRCFailed(Exception e);
        void onIRCFinished(DCC dcc);
    }
}
