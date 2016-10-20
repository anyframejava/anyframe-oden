package anyframe.oden.bundle.deploy;

public interface DeployerFactory {
	public String getProtocol();
	public DeployerService newInstance(String addr);
}
