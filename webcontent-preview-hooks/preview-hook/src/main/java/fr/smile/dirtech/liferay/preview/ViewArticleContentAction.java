package fr.smile.dirtech.liferay.preview;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.Parameters;
import org.esigate.aggregator.AggregateRenderer;
import org.esigate.esi.EsiRenderer;
import org.esigate.servlet.HttpServletMediator;

import com.liferay.portal.kernel.struts.BaseStrutsAction;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

/**
 * Overrides the default ViewArticleContentAction
 * Calls the original action, and in case of article preview, gets the original layout and inject the article new content via Esigate
 *
 */
public class ViewArticleContentAction extends BaseStrutsAction {

	/* (non-Javadoc)
	 * @see com.liferay.portal.kernel.struts.BaseStrutsAction#execute(com.liferay.portal.kernel.struts.StrutsAction, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public String execute(StrutsAction originalStrutsAction,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String originalForward = originalStrutsAction
				.execute(request, response);
		String cmd = ParamUtil.getString(request, Constants.CMD);
		
		if (Constants.PREVIEW.equals(cmd)) {
			ThemeDisplay td = (ThemeDisplay) request
					.getAttribute(WebKeys.THEME_DISPLAY);
			long originalPlid = td.getPlid();
			String page = PortalUtil.getLayoutActualURL(LayoutLocalServiceUtil
					.getLayout(originalPlid));
			Driver driver = DriverFactory.getInstance();

			Properties props = getEsigateProperties();

			DriverFactory.configure(props);
			HttpEntityEnclosingRequest httpRequest = new HttpServletMediator(
					request, response, request.getSession().getServletContext())
					.getHttpRequest();
			Map<String, String> parameters = new HashMap<String, String>();

			EsiRenderer esiRenderer = new EsiRenderer();
			Map<String, CharSequence> fragments = new HashMap<String, CharSequence>();
			fragments.put("article_preview",
					((String) request.getAttribute("JOURNAL_ARTICLE_CONTENT")));
			esiRenderer.setFragmentsToReplace(fragments);
			
			driver.render(page, parameters, response.getWriter(), httpRequest,
					new AggregateRenderer(), esiRenderer);
			return null;
		}

		return originalForward;

	}

	/**
	 * Builds Esigate configuration
	 * @return
	 */
	private Properties getEsigateProperties() {
		Properties props = new Properties();
		String remoteUrlBase = PropsUtil.get("journal.preview.url.base");
		if (StringUtils.isEmpty(remoteUrlBase)) {
			remoteUrlBase = "http://localhost:8080";
		}
		props.put(Parameters.REMOTE_URL_BASE.name, remoteUrlBase);
		props.put(Parameters.PRESERVE_HOST.name, "true");
		props.put(Parameters.USE_CACHE.name, "false");
		props.put(Parameters.FILTER.name,
				"org.esigate.filter.CookieForwardingFilter");
		props.put(Parameters.FORWARD_COOKIES.name, "*");
		return props;
	}

}
