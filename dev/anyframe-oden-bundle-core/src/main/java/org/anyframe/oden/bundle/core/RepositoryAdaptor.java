package org.anyframe.oden.bundle.core;

import org.anyframe.oden.bundle.common.FatInputStream;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.core.repository.RepositoryService;

public class RepositoryAdaptor {
	RepositoryService svc;
	String[] args;
	
	public RepositoryAdaptor(RepositoryService svc, String[] args){
		this.svc = svc;
		this.args = args;
	}
	
	public FatInputStream resolve(String file) throws OdenException{
		return svc.resolve(args, file);
	}
	
	public void close(){
		svc.close(args);
	}
}
