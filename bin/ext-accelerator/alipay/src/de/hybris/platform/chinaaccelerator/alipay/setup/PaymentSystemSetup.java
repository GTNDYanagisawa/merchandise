package de.hybris.platform.chinaaccelerator.alipay.setup;

import java.util.ArrayList;
import java.util.List;


//import de.hybris.platform.acceleratorservices.setup.AbstractSystemSetup;
import de.hybris.platform.chinaaccelerator.alipay.constants.AlipayConstants;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;


@SystemSetup(extension = AlipayConstants.EXTENSIONNAME)
public class PaymentSystemSetup extends AbstractSystemSetup
{
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		importImpexFile(context, "/alipay/import/essential/payments.impex");
	}

	@Override
	public List<SystemSetupParameter> getInitializationOptions() {
		return new ArrayList<SystemSetupParameter>();
	}
}
