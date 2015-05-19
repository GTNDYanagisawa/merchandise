/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package mypackage;

@SuppressWarnings("serial")
public class MyBrowserComponent extends AbstractBrowserComponent
{
	protected transient Borderlayout mainArea = null;
	protected transient Label label = null;

	public MyBrowserComponent(final AbstractBrowserModel model, final ContentBrowserController controller)
	{
		super(model, controller);
	}

	@Override
	public boolean initialize()
	{
		this.initialized = false;
		if (this.getModel() != null)
		{
			this.setHeight("100%");
			this.setWidth("100%");

			this.getChildren().clear();

			this.mainArea = createMainArea();
			this.appendChild(mainArea);
		}
		this.initialized = true;
		return true;
	}

	@Override
	public boolean update()
	{
		// synchronize view with underlying model
		this.label.setValue(String.valueOf(this.getModel().isFoo()));

		return true;
	}

	@Override
	public void resize()
	{
		if (this.initialized)
		{
			this.mainArea.resize();
		}
	}

	@Override
	public void setActiveItem(final Object activeItem)
	{
		//TODO why empty?
	}

	@Override
	public void updateActiveItems()
	{
		//TODO why empty?
	}

	protected Borderlayout createMainArea()
	{
		final Borderlayout area = new Borderlayout();
		area.setWidth("100%");
		area.setHeight("100%");
		area.setSclass(MAIN_AREA_BL_SCLASS);

		final Center center = new Center();
		area.appendChild(center);

		this.label = new Label(String.valueOf(this.getModel().isFoo()));
		center.appendChild(this.label);

		final North north = new North();
		area.appendChild(north);

		final Button toggleBtn = new Button("Toggle!");
		north.appendChild(toggleBtn);
		toggleBtn.addEventListener(Events.ON_CLICK, new EventListener()
		{
			public void onEvent(final Event event) throws Exception
			{
				MyBrowserComponent.this.getModel().setFoo(!MyBrowserComponent.this.getModel().isFoo());
			}
		});

		return area;
	}

	@Override
	public MyBrowserModel getModel()
	{
		return (MyBrowserModel) super.getModel();
	}
}
