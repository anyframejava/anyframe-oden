package anyframe.oden.bundle.samsunglife;

import java.util.List;

import anyframe.oden.bundle.core.record.RecordElement2;

/**
 * 
 * 1. Oden deploy to spectrum.
 * 2. Spectrum send status to Oden.
 * 3. Oden notify to spectrum what is deploy status.
 * 
 * Oden notify to spectrum what received status about deploy.
 * 
 * @author joon1k
 * 
 */
public interface SpectrumNotifier {

	public boolean notifyResult(String txid);

	public boolean notifyResult(RecordElement2 record);

	public List<String> notifiedIds();
}
