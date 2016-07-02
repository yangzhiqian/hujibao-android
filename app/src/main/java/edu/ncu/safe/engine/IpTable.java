package edu.ncu.safe.engine;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Mr_Yang on 2016/5/24.
 */
public final class IpTable {
    public static final long timeout = 10000;
    public static final String PERFS_NAME = "IPTABLES";
    public static final String PERFS_WIFI = "WIFIS";
    public static final String PERFS_GPRS = "GPRS";

    private static boolean updateBlackIPTable(Context ctx, List<Integer> uidsWIFI, List<Integer> uidsGPRS) {
        if (ctx == null) {
            return false;
        }
        final String WIFIS[] = {"tiwlan+", "wlan+", "eth+"};
        final String GPRSS[] = {"rmnet+", "pdp+", "ppp+", "uwbr+", "wimax+"};

        try {
            final StringBuilder script = new StringBuilder();
            clearIpTable(ctx);
            for (final Integer uid : uidsGPRS) {
                if (uid >= 0) {
                    for (final String gprs : GPRSS) {
                        script.append("iptables  -A OUTPUT -o "+gprs+"  -m owner --uid-owner " + uid + " -j DROP ").append(" || exit\n");
                    }
                }
            }
            for (final Integer uid : uidsWIFI) {
                if (uid >= 0) {
                    for (final String wifi : WIFIS) {
                        script.append("iptables  -A OUTPUT -o "+wifi+"  -m owner --uid-owner " + uid + " -j DROP ").append(" || exit\n");
                    }
                }
            }
            script.append("service iptables restart\n");
           // System.out.println("命令：\n"+script.toString());
            final StringBuffer sb = new StringBuffer();
           int re = runScript(ctx, script.toString(),sb);
           // System.out.println("返回情况：\n"+sb.toString());
            if (re != 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean updateBlackIPTable(Context context) {
        if(hasRootAccess(context)==false){
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(PERFS_NAME, Context.MODE_PRIVATE);
        List<Integer> uidsGPRS = new ArrayList<Integer>();
        StringTokenizer tokenizer = new StringTokenizer(sp.getString(PERFS_GPRS, "").toString().trim(), "|");
        while (tokenizer.hasMoreTokens()) {
            uidsGPRS.add(Integer.parseInt(tokenizer.nextToken()));
        }

        List<Integer> uidsWIFI = new ArrayList<Integer>();
        tokenizer = new StringTokenizer(sp.getString(PERFS_WIFI, "").toString().trim(), "|");
        while (tokenizer.hasMoreTokens()) {
            uidsWIFI.add(Integer.parseInt(tokenizer.nextToken()));
        }
        return updateBlackIPTable(context, uidsWIFI, uidsGPRS);
    }

    public static List<Integer> loadIpTableItems(Context context){
        List<Integer> uids = new ArrayList<Integer>();
        final StringBuffer sb = new StringBuffer();
        if (runScript(context, "iptables -L ",sb) == 0) {
            //获取成功
            System.out.println("获取成功：\n"+sb.toString());
        }
        return uids;
    }

    public static void clearIpTable(Context context){
        runScript(context, "iptables -F ",new StringBuffer());
    }

    public static boolean hasRootAccess(Context ctx) {
        try {
            if (runScript(ctx, "exit 0",new StringBuffer()) == 0) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private static int runScript(Context ctx, String script,StringBuffer sb) {

        final File file = new File(ctx.getCacheDir(), "script.sh");
        final ScriptRunner runner = new ScriptRunner(file, script,sb);
        runner.start();
        try {
            if (timeout > 0) {
                runner.join(timeout);
            } else {
                runner.join();
            }
            if (runner.isAlive()) {
                // Timed-out
                runner.interrupt();
                runner.join(150);
                runner.destroy();
                runner.join(50);
            }
        } catch (InterruptedException ex) {
        }
        return runner.exitcode;
    }

    /**
     * Internal thread used to execute scripts (as root or not).
     */
    private static final class ScriptRunner extends Thread {
        private final File file;
        private final String script;
        public int exitcode = -1;
        private Process exec;
        private StringBuffer sb;

        /**
         * Creates a new script runner.
         *
         * @param file   temporary script file
         * @param script script to run
         */
        public ScriptRunner(File file, String script,StringBuffer sb) {
            this.file = file;
            this.script = script;
            this.sb = sb;
        }

        @Override
        public void run() {
            try {
                file.createNewFile();
                final String abspath = file.getAbsolutePath();
                Runtime.getRuntime().exec("chmod 777 " + abspath).waitFor();// make sure we have execution permission on the script file
                final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file));// Write the script to be executed
                if (new File("/system/bin/sh").exists()) {
                    out.write("#!/system/bin/sh\n");
                }
                out.write(script);
                if (!script.endsWith("\n"))
                    out.write("\n");
                out.write("exit\n");
                out.flush();
                out.close();
                // Create the "su" request to run the script
                exec = Runtime.getRuntime().exec("su -c " + abspath);
                InputStreamReader r = new InputStreamReader(exec.getInputStream());
                final char buf[] = new char[1024];
                int read = 0;
                // Consume the "stdout"
                while ((read = r.read(buf)) != -1) {
                    if (sb != null)
                        sb.append(buf, 0, read);
                }
                // Consume the "stderr"
                r = new InputStreamReader(exec.getErrorStream());
                read = 0;
                while ((read = r.read(buf)) != -1) {
                    if (sb != null)
                        sb.append(buf, 0, read);
                }
                // get the process exit code
                if (exec != null)
                    this.exitcode = exec.waitFor();
            } catch (Exception ex) {
            } finally {
                destroy();
            }
        }

        public synchronized void destroy() {
            if (exec != null)
                exec.destroy();
            exec = null;
        }
    }
}