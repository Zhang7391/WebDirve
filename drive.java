package taiwan.webpage;

import org.apache.wicket.request.Url;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.wicketstuff.annotation.mount.MountPath;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;

import taiwan.webpage.login;
import taiwan.webpage.operator;
import taiwan.toolbox.LoginCheck;
import taiwan.formwork.html.Titlebar;
import taiwan.formwork.html.Copyright;


@MountPath("/drive")
public class drive extends WebPage
{
	public drive()
	{
		super();

		LoginCheck loginCheck = new LoginCheck((WebPage)this);
		
		boolean key = true;
		if(!loginCheck.verify()) 
		{
			key = false;
			this.setResponsePage(login.class);
		}

		if(key) loginCheck.reply(this.getPageClass());
	}

	public drive(PageParameters pageParameters) 
	{
		super(pageParameters);

		LoginCheck loginCheck = new LoginCheck((WebPage)this);

		boolean key = true;
		if(!loginCheck.verify())
		{
			key = false;
			this.setResponsePage(login.class);
		}

		if(key && !pageParameters.get("codename").toString().contentEquals(loginCheck.getUsername()))
			key = loginCheck.reply(this.getPageClass());
		
		if(key) 
		{
			add(new Titlebar("titlebar", "\u500b\u4eba\u96f2\u7aef\u786c\u789f", true, pageParameters, operator.class));

			add(new Copyright("copyright"));
		}
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		Url url = this.getRequestCycle().get().getRequest().getUrl();
		String fullUrl = this.getRequestCycle().get().getUrlRenderer().renderFullUrl(url);
		String rootUrl = fullUrl.substring(0, fullUrl.indexOf('/', fullUrl.indexOf('/')+2));

		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/global.css"));
		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/css/drive.css"));
		response.render(JavaScriptReferenceHeaderItem.forUrl(rootUrl + "/global.js"));
		response.render(JavaScriptReferenceHeaderItem.forUrl(rootUrl + "/js/drive.js"));
		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/thirdtools/PGcalculator/free.min.css"));
	}
}
