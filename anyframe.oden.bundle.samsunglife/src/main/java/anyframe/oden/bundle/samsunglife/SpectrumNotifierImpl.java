package anyframe.oden.bundle.samsunglife;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.osgi.service.component.ComponentContext;

import anyframe.oden.bundle.common.Assert;
import anyframe.oden.bundle.common.Logger;
import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.core.DeployFile;
import anyframe.oden.bundle.core.record.DeployLogService;
import anyframe.oden.bundle.core.record.RecordElement2;

import com.caucho.hessian.client.HessianProxyFactory;
import com.spectrum.spch.service.ITrncCiTrtRsltService;
import com.spectrum.spch.vo.TrncCiTrtRsltVO;
/**
 * 
 * @see anyframe.oden.bundle.samsunglife.SpectrumNotifier
 * 
 * @author joon1k
 *
 */
public class SpectrumNotifierImpl implements SpectrumNotifier {
	
	private String SPECTRUM_URL;
	
	private SpectrumRecorder recorder = new SpectrumRecorder();
	
	private ITrncCiTrtRsltService svc;
	
	private DeployLogService deploylog;
	
	protected void setDeployLogService(DeployLogService svc) {
		this.deploylog = svc;
	}
	
	protected void activate(ComponentContext context){
		SPECTRUM_URL = context.getBundleContext().getProperty("spectrum.url");
	}
	
	private ITrncCiTrtRsltService getOdenService(){
		if(svc == null){
			try {
				Assert.check(SPECTRUM_URL != null, "spectrum.url is not defined in the oden.ini");
				svc = (ITrncCiTrtRsltService) new HessianProxyFactory().create(
						Class.forName(ITrncCiTrtRsltService.class.getName()), 
						SPECTRUM_URL,
						this.getClass().getClassLoader());
			} catch (Exception e) {
				Logger.error(e);
				return null;
			}
		}
		return svc;
	}
	
	private boolean notifyResult(List<TrncCiTrtRsltVO> vos){
		ITrncCiTrtRsltService svc = getOdenService();
		if(svc == null)
			return false;
		
		try{
			if(!svc.registerTrncCiTrtRsltList(vos))
				return false;
		}catch(Exception e){
			Logger.error(e);
			return false;
		}
		return true;
	}
	
	public boolean notifyResult(String txid) {
		try {
			Assert.check(txid != null && txid.length() > 0, "Transaction Id is required.");
			RecordElement2 r = deploylog.search(txid, null, null, null, false);
			if(r == null)
				return false;
			
			return notifyResult(r);
		} catch (OdenException e) {
			Logger.error(e);
		}
		return false;
	}
	
	public List<String> notifiedIds(){
		return recorder.loadIds();
	}
	
	public boolean notifyResult(RecordElement2 record) {
		List<TrncCiTrtRsltVO> vos = convert(record);
		if(notifyResult(vos)){
			try {
				recorder.writeId(record.id());
			} catch (IOException e) {
				Logger.error(e);
			}
			return true;
		}
		return false;
	}

	private List<TrncCiTrtRsltVO> convert(RecordElement2 log) {
		List<TrncCiTrtRsltVO> vos = new ArrayList<TrncCiTrtRsltVO>();
		
		Format fmt = new DecimalFormat("000000");
		
		int seq = 0;
		for(DeployFile f : log.getDeployFiles()){
			SpectrumComment info = null;
			try {
				info = new SpectrumComment(f.comment());
			} catch (OdenException e) {
				info = new SpectrumComment("", "", "", "", "");
			}
			
			TrncCiTrtRsltVO v = new TrncCiTrtRsltVO();
			String sid = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(Long.valueOf(log.id()) ) );
			v.setTrtLogId(sid + fmt.format(seq));	//신청대상처리로그ID
			v.setAplId(info.getResourceId());				//신청대상ID ==> <chgApplyId> 값   
			v.setTrncSvrGrp(info.getServerGroup());			//배포서버그룹 ==> <targetServerGrp> 값
		    v.setTrncSvrOpratScCd(info.getOperCd());		//배포서버운영구분코드==> 운영 : PRD, 검증계 : TST, 개발계 : DEV 
			v.setTrncSvrNm(info.getServer());				//배포서버명 ==> <serverIP> 값
		    v.setTrtDtlRsltCd(f.isSuccess() ? "00" : "12"); //작업결과코드 ==> 성공(00), 실패(12): 추가 코드는 추후
		    v.setTrtRsltCntnt(f.errorLog() == null ? "" : f.errorLog());     					//작업결과내용 ==> Null
		    v.setLstChgDtm(new Timestamp(log.getDate()) );  //최종변경일시 ==> ODEN History에 있는 배포시각
		    v.setLstMdfrId(log.getUser());          		//최종변경자 ==> 현재는 ODEN 작업자 IP
			vos.add(v);
			
			seq++;
		}
		return vos;
	}
}
