/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.common.bundle.gate;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.felix.shell.Command;
import org.apache.felix.shell.impl.BundleLevelCommandImpl;
import org.apache.felix.shell.impl.CdCommandImpl;
import org.apache.felix.shell.impl.ExportsCommandImpl;
import org.apache.felix.shell.impl.HeadersCommandImpl;
import org.apache.felix.shell.impl.HelpCommandImpl;
import org.apache.felix.shell.impl.ImportsCommandImpl;
import org.apache.felix.shell.impl.InstallCommandImpl;
import org.apache.felix.shell.impl.PsCommandImpl;
import org.apache.felix.shell.impl.RefreshCommandImpl;
import org.apache.felix.shell.impl.RequirersCommandImpl;
import org.apache.felix.shell.impl.RequiresCommandImpl;
import org.apache.felix.shell.impl.ResolveCommandImpl;
import org.apache.felix.shell.impl.ServicesCommandImpl;
import org.apache.felix.shell.impl.ShutdownCommandImpl;
import org.apache.felix.shell.impl.StartCommandImpl;
import org.apache.felix.shell.impl.StartLevelCommandImpl;
import org.apache.felix.shell.impl.StopCommandImpl;
import org.apache.felix.shell.impl.UninstallCommandImpl;
import org.apache.felix.shell.impl.UpdateCommandImpl;
import org.apache.felix.shell.impl.VersionCommandImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @see org.apache.felix.shell.impl.Activator
 * 
 */
public class Activator implements BundleActivator {
    private transient BundleContext m_context = null;
    private transient ShellGate m_shell = null;
    private transient LogService log = null;
    private transient Set<String> availableCmds = null;

	public void start(BundleContext context) throws Exception {
		 m_context = context;
 
	        // Register impl service implementation.
	        String[] classes = {
	            org.apache.felix.shell.ShellService.class.getName(),
	            org.ungoverned.osgi.service.shell.ShellService.class.getName()
	        };
	        context.registerService(classes, m_shell = new ShellGate(), null);

	        // Listen for registering/unregistering of impl command
	        // services so that we can automatically add/remove them
	        // from our list of available commands.
	        ServiceListener sl = new ServiceListener() {
	            public void serviceChanged(ServiceEvent event)
	            {
	                if (event.getType() == ServiceEvent.REGISTERED)
	                {
	                    m_shell.addCommand(event.getServiceReference());
	                }
	                else if (event.getType() == ServiceEvent.UNREGISTERING)
	                {
	                    m_shell.removeCommand(event.getServiceReference());
	                }
	                else
	                {
	                }
	            }
	        };

	        try
	        {
	            m_context.addServiceListener(sl,
	                "(|(objectClass="
	                + org.apache.felix.shell.Command.class.getName()
	                + ")(objectClass="
	                + org.ungoverned.osgi.service.shell.Command.class.getName()
	                + ")(objectClass="
	                + CustomCommand.class.getName()
	                + "))");
	        }
	        catch (InvalidSyntaxException ex)
	        {
	            System.err.println("Activator: Cannot register service listener.");
	            System.err.println("Activator: " + ex);
	        }

	        // Now manually try to find any commands that have already
	        // been registered (i.e., we didn't see their service events).
	        initializeCommands();

	        // Register "bundlelevel" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new BundleLevelCommandImpl(m_context), null);

	        // Register "cd" command service.
	        classes = new String[2];
	        classes[0] = org.apache.felix.shell.Command.class.getName();
	        classes[1] = org.apache.felix.shell.CdCommand.class.getName();
	        context.registerService(
	            classes, new CdCommandImpl(m_context), null);

	        // Register "exports" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new ExportsCommandImpl(m_context), null);

	        // Register "headers" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new HeadersCommandImpl(m_context), null);

	        // Register "help" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new HelpCommandImpl(m_context), null);

	        // Register "imports" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new ImportsCommandImpl(m_context), null);

	        // Register "install" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new InstallCommandImpl(m_context), null);

	        // Register "ps" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new PsCommandImpl(m_context), null);

	        // Register "refresh" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new RefreshCommandImpl(m_context), null);

	        // Register "requires" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new RequiresCommandImpl(m_context), null);

	        // Register "requirers" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new RequirersCommandImpl(m_context), null);

	        // Register "resolve" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new ResolveCommandImpl(m_context), null);

	        // Register "services" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new ServicesCommandImpl(m_context), null);

	        // Register "startlevel" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new StartLevelCommandImpl(m_context), null);

	        // Register "shutdown" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new ShutdownCommandImpl(m_context), null);

	        // Register "start" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new StartCommandImpl(m_context), null);

	        // Register "stop" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new StopCommandImpl(m_context), null);

	        // Register "uninstall" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new UninstallCommandImpl(m_context), null);

	        // Register "update" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new UpdateCommandImpl(m_context), null);

	        // Register "version" command service.
	        context.registerService(
	            org.apache.felix.shell.Command.class.getName(),
	            new VersionCommandImpl(m_context), null);
	        
	        loadAvailableCommandNames(context);
	}

	private void loadAvailableCommandNames(BundleContext context) {
		String names = context.getProperty("cmd.available");
		if(names == null){
			// Do not return empty collection!!
			availableCmds = null;
		} else {
			availableCmds = new HashSet<String>();
			for(String s: names.split("\\s"))
				availableCmds.add(s);
		}
	}

	public void stop(BundleContext context)
    {
        m_shell.clearCommands();
    }

    private void initializeCommands()
    {
        synchronized (m_shell)
        {
            try
            {
                ServiceReference[] refs = m_context.getServiceReferences(
                    org.apache.felix.shell.Command.class.getName(), null);
                if (refs != null)
                {
                    for (int i = 0; i < refs.length; i++)
                    {
                        m_shell.addCommand(refs[i]);
                    }
                }
            }
            catch (Exception ex)
            {
                System.err.println("Activator: " + ex);
            }
        }
    }
    
    protected void debug(String msg) {
    	if(log == null){
	    	ServiceReference ref = m_context.getServiceReference(
					 org.osgi.service.log.LogService.class.getName());
	    	Object svc = m_context.getService(ref);
	    	if(svc == null || !(svc instanceof LogService)){
	    		String level = m_context.getProperty("felix.log.level");
	    		if(level != null && Integer.parseInt(level) <= LogService.LOG_DEBUG)
	    			System.out.println("[DEBUG] " + msg);
	    		return;
	    	}
	    	log = (LogService)svc;
    	}
		log.log(LogService.LOG_DEBUG, msg);
    }

    private class ShellGate implements
        org.apache.felix.shell.ShellService,
        org.ungoverned.osgi.service.shell.ShellService
    {
        private Map m_commandRefMap = 
        		Collections.synchronizedMap(new HashMap());
        private Map m_commandNameMap = 
        		Collections.synchronizedMap(new TreeMap());

        /**
         * help명령어시 보여질 command들 추가
         */
        public synchronized String[] getCommands()
        {
            Set<String> ks = m_commandNameMap.keySet();
            List<String> cmds = new ArrayList<String>(); 
            for(String name : ks){
            	Command cmd = (Command) m_commandNameMap.get(name);
            	if(availableCmds == null){	// oden 1.x
            		if(cmd instanceof CustomCommand)
            			cmds.add(name);
            	}else{		// oden 2.x
            		if(availableCmds.contains(cmd.getName()))
	            		cmds.add(name);
            	}
            }
            
            if(availableCmds != null && cmds.size() != availableCmds.size())
            	throw new RuntimeException("Fail to load: " 
            			+ notLoadedCommands(availableCmds, cmds));
            return cmds.toArray(new String[cmds.size()]);
        }
        
        private Set<String> notLoadedCommands(Set<String> available, 
        		List<String> loaded){
        	Set<String> ret = new HashSet<String>(available);
        	for(String c : loaded)
        		ret.remove(c);
        	return ret;
        }
        
        public synchronized String getCommandUsage(String name)
        {
            Command command = (Command) m_commandNameMap.get(name);
            return (command == null) ? null : command.getUsage();
        }

        public synchronized String getCommandDescription(String name)
        {
            Command command = (Command) m_commandNameMap.get(name);
            return (command == null) ? null : command.getShortDescription();
        }

        public synchronized ServiceReference getCommandReference(String name)
        {
            ServiceReference ref = null;
            Iterator itr = m_commandRefMap.entrySet().iterator();
            while (itr.hasNext())
            {
                Map.Entry entry = (Map.Entry) itr.next();
                if (((Command) entry.getValue()).getName().equals(name))
                {
                    ref = (ServiceReference) entry.getKey();
                    break;
                }
            }
            return ref;
        }

        public synchronized void removeCommand(ServiceReference ref)
        {
            Command command = (Command) m_commandRefMap.remove(ref);
            if (command != null)
            {
                m_commandNameMap.remove(command.getName());
            }
        }

        @SuppressWarnings("unchecked")
		public void executeCommand(
            String commandLine, PrintStream out, PrintStream err) throws Exception
        {
            commandLine = commandLine.trim();
            String commandName = (commandLine.indexOf(' ') >= 0)
                ? commandLine.substring(0, commandLine.indexOf(' ')) : commandLine;
            Command command = getCommand(commandName);
            if (command != null)
            {
                if (System.getSecurityManager() != null)
                {
                    try
                    {
                    	synchronized (this) {
                            AccessController.doPrivileged(
                                    new ExecutePrivileged(command, commandLine, out, err));							
						}
                    }
                    catch (PrivilegedActionException ex)
                    {
                        throw ex.getException();
                    }
                }
                else
                {
                    try
                    {
                    	debug("Command executed: " + commandLine);
                    	
                        command.execute(commandLine, out, err);
                    }
                    catch (Throwable ex)
                    {
                        err.println("Unable to execute command: " + ex);
                        ex.printStackTrace(err);
                    }
                }
            }
            else if(commandLine.length() > 0)
            {
                err.println("Command not found. Type 'help' for usage.");
            }
        }

        protected synchronized Command getCommand(String name)
        {
            Command command = (Command) m_commandNameMap.get(name);
            return (command == null) ? null : command;
        }

        protected synchronized void addCommand(ServiceReference ref)
        {
            Object cmdObj = m_context.getService(ref);
            
            Command command = null;
            if(cmdObj instanceof CustomCommand){
            	command = new CustomCommandWrapper((CustomCommand)cmdObj);
            }else if(cmdObj instanceof org.ungoverned.osgi.service.shell.Command){
            	command = new OldCommandWrapper((org.ungoverned.osgi.service.shell.Command)cmdObj);
            }else {
            	command = (Command) cmdObj;
            }
            m_commandRefMap.put(ref, command);
            m_commandNameMap.put(command.getName(), command);
        }

        protected synchronized void clearCommands()
        {
            m_commandRefMap.clear();
            m_commandNameMap.clear();
        }
    }

    private static class CustomCommandWrapper implements Command, CustomCommand {
    	private CustomCommand customCommand = null;

        public CustomCommandWrapper(CustomCommand customCommand)
        {
            this.customCommand = customCommand;
        }

        public String getName()
        {
            return customCommand.getName();
        }

        public String getUsage()
        {
            return customCommand.getUsage();
        }

        public String getShortDescription()
        {
            return customCommand.getShortDescription();
        }

        public void execute(String line, PrintStream out, PrintStream err)
        {
            customCommand.execute(line, out, err);
        }
    }
    
    private static class OldCommandWrapper implements Command
    {
        private org.ungoverned.osgi.service.shell.Command m_oldCommand = null;

        public OldCommandWrapper(org.ungoverned.osgi.service.shell.Command oldCommand)
        {
            m_oldCommand = oldCommand;
        }

        public String getName()
        {
            return m_oldCommand.getName();
        }

        public String getUsage()
        {
            return m_oldCommand.getUsage();
        }

        public String getShortDescription()
        {
            return m_oldCommand.getShortDescription();
        }

        public void execute(String line, PrintStream out, PrintStream err)
        {
            m_oldCommand.execute(line, out, err);
        }
    }

    public static class ExecutePrivileged implements PrivilegedExceptionAction
    {
        private Command m_command = null;
        private String m_commandLine = null;
        private PrintStream m_out = null;
        private PrintStream m_err = null;

        public ExecutePrivileged(
            Command command, String commandLine,
            PrintStream out, PrintStream err)
            throws Exception
        {
            m_command = command;
            m_commandLine = commandLine;
            m_out = out;
            m_err = err;
        }

        public Object run() throws Exception
        {
            m_command.execute(m_commandLine, m_out, m_err);
            return null;
        }
    }

}
