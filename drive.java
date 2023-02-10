package taiwan.webpage;

import javax.servlet.http.Cookie;
import org.apache.wicket.request.Url;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse; 
import org.wicketstuff.annotation.mount.MountPath;
import org.apache.wicket.request.cycle.RequestCycle;
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
	private String username = null;

	public drive()
	{
		super();

		boolean key = this.cookieChecker();
		if(key) 
		{
			PageParameters pageParameters = new PageParameters();
			pageParameters.add("codename", this.username);

			this.setResponsePage(new drive(pageParameters));
		}
		else this.setResponsePage(login.class);
	}

	public drive(PageParameters pageParameters) 
	{
		super(pageParameters);

		boolean key = this.cookieChecker();
		
		if(key && pageParameters.get("codename").isNull())
		{
			key = false;
			this.setResponsePage(login.class);
		}
		
		if(key) 
		{
			pageParameters.add("user", pageParameters.get("codename").toString());
			add(new Titlebar("titlebar", "\u500b\u4eba\u96f2\u7aef\u786c\u789f", true, pageParameters, operator.class));
			pageParameters.remove("user");

			add(new Copyright("copyright"));
		}
		else this.setResponsePage(login.class);
	}

	private boolean cookieChecker()
	{
		WebRequest request = (WebRequest) RequestCycle.get().getRequest();
		Cookie cookie = request.getCookie("passingID");

		boolean key = true;
		
		LoginCheck loginCheck = new LoginCheck((WebPage)this, cookie);
		this.username = loginCheck.getUsername();
		
		if(cookie != null)
		{
			if(!loginCheck.logging())
			{
				cookie.setMaxAge(0);

				WebResponse webResponse = (WebResponse)RequestCycle.get().getResponse();
				webResponse.addCookie(cookie);

				key = false;
			}
			
//			key = loginCheck.reply(index.class, "user");
		}
		else key = false;

		return key;
	}

/*	public drive(PageParameters pageParameters, boolean lawful)
	{
		if(lawful) 
		{
			add(new Titlebar("titlebar", "\u9996\u9801", true, pageParameters, this.getPageClass()));

			add(new Copyright("copyright"));
			
			add(new Label("text", "Logined.").setRenderBodyOnly(true));
		}
	}*/

	@Override
	public void renderHead(IHeaderResponse response)
	{
		Url url = RequestCycle.get().getRequest().getUrl();
		String fullUrl = RequestCycle.get().getUrlRenderer().renderFullUrl(url);

		String rootUrl;
		try {rootUrl = fullUrl.substring(0, fullUrl.indexOf('/', fullUrl.indexOf('/')+2));}
		catch(Exception error) {rootUrl = fullUrl;}

		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/global.css"));
		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/css/index.css"));
		response.render(JavaScriptReferenceHeaderItem.forUrl(rootUrl + "/global.js"));
		response.render(JavaScriptReferenceHeaderItem.forUrl(rootUrl + "/js/index.js"));
		response.render(CssReferenceHeaderItem.forUrl(rootUrl + "/thirdtools/PGcalculator/free.min.css"));
	}
}
