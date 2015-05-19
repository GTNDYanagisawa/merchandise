/**
 * 
 */
package de.hybris.platform.chinaaccelerator.facades.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;



public class AddressPopulator extends de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator
{
	private static final Logger LOG = Logger.getLogger(AddressPopulator.class);


	@Override
	public void populate(final AddressModel source, final AddressData target)
	{
		super.populate(source, target);

		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");


		if (source.getRemarks() != null)
		{
			target.setRemarks(source.getRemarks());
		}
		if (source.getCellphone() != null)
		{
			target.setCellphone(source.getCellphone());
		}
		if (source.getCityDistrict() != null)
		{
			target.setCityDistrict(source.getCityDistrict().getName());
		}
		if (source.getCity() != null)
		{
			target.setCity(source.getCity().getName());
		}

		if (source.getPhone1() != null)
		{
			// phone2?
			// assumed pattern xxx-xxxx-xxx with 2 hyphens

			final StringTokenizer tok = new StringTokenizer(source.getPhone1(), "-");
			if (tok.countTokens() == 1)
			{
				// store to middle part (assumption)
				target.setLandlinePhonePart2(tok.nextToken());
			}
			else
			{
				// store from beginning
				int i = 1;
				while (tok.hasMoreTokens())
				{
					final String phonePart = tok.nextToken();
					switch (i)
					{
						case 1:
							target.setLandlinePhonePart1(phonePart);
							i++;
							break;
						case 2:
							target.setLandlinePhonePart2(phonePart);
							i++;
							break;
						case 3:
							target.setLandlinePhonePart3(phonePart);
							i++;
							break;
						default:
							break;
					}
				}
			}
		}

	}
}
