package dataHandler;

public class Informations {

    public String getTextCommands() {
        String text = " -> '/close'-close chat(terminate of process);\r\n" + 
                " -> '/clear' - clear chat;\r\n" +
                " -> '/getinfo' - get statistics;"
                + " -> /addfriend username - add new friend"
                + " -> /accept username - accept friend invite sent by username"
                + " -> /decline username - decline friend invite sent by username";
        return text;
    }

    public String getTextFixBugs() {

        String text = " -> Created a close button to close and shutdown the process;\n"
                + " -> Created a button to access more info;\n"
                + " -> Now users can see the Version of the app;\n"
                + " -> Cursors have been updated;\n"
                + " -> Now users can scroll the text of chat and the list of connections;\n"
                + " -> Users now can reply diretly only with '/r' (Read Commands section for more info);\n"
                + " -> Fixed bug where users could send messages to themselfs.\n"
                + " -> Launcher has changed!\n"
                + " -> Now Live News are live! Read them!\n"
                + " -> Now you can't say rubbish!!!\n"
                + " -> NEW visual update!\n"
                + " -> Now is displayed time of messages.\n"
                + " -> New Launcher!\n"
                + " -> NEW statistics!\n"
                + " -> NOW you can have friends! Go make friends right now!";

        return text;
    }

}
