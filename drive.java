package taiwan.webpage;

import java.io.File;
import org.apache.wicket.request.Url;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.wicketstuff.annotation.mount.MountPath;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
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

			add(new FilesUpload("upload_zone"));
		}
	}

	private class FilesUpload extends Form
	{
		private String code;

		public FilesUpload(String id)
		{
			super(id);

			FileUploadField field = new FileUploadField("fileButton");
			add(field);
			
			add(new AjaxButton("search")
			{
				@Override
				protected void onSubmit(AjaxRequestTarget target)
				{
					System.out.println("Recive Files.");

					for(FileUpload file : field.getFileUploads())
					{
						try
						{
							System.out.println("/mnt/" + file.getClientFileName());
							file.writeTo(new File("/mnt/" + file.getClientFileName()));
						}
						catch(Exception error) {error.printStackTrace();}
					}
				}
			});

			this.setMultiPart(true);
			this.setOutputMarkupId(true);

/*			final Model<String> model = new Model<String>()
			{
				private String location = "No message";

				@Override
				public String getObject() {return this.location;}

				@Override
				public void setObject(String location) {this.location = location;}
			};

			final Label label = new Label("location", model);
			label.setOutputMarkupId(true);
			add(label);
			
			AjaxButton testButton = new AjaxButton("search")
			{
				@Override
				protected void onSubmit(AjaxRequestTarget target)
				{
					System.out.println("Recive Files.");

					try
					{
						System.out.println("/mnt/" + field.getFileUpload().getClientFileName());
						field.getFileUpload().writeTo(new File("/mnt/" + field.getFileUpload().getClientFileName()));
					}
					catch(Exception error) {error.printStackTrace();}

					model.setObject("GET!");
					
					target.add(label);
				}
			};
			add(testButton);*/
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
