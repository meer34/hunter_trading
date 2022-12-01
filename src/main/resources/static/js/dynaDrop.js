
function onProductChange(productName) {
	var sizeTag = '<option selected disabled="disabled" value="">Size</option>';
	var colourTag = '<option selected disabled="disabled" value="">Colour</option>';
	var brandTag = '<option selected disabled="disabled" value="">Brand</option>';
	var productTag = '<option selected disabled="disabled" value="">Product Type</option>';
	if (productName) {
		$.ajax({
			url : '/loadSizesByProductName',
			data : { "productName" : productName },
			success : function(result) {
				var result = JSON.parse(result);
				for (var i = 0; i < result.length; i++) {
					sizeTag += '<option value="' + result[i] + '">'+ result[i]+ '</option>';
				}
				$('#sizes').html(sizeTag);
			}
		});
	}
	//reset data
	$('#sizes').html(sizeTag);
	$('#colours').html(colourTag);
	$('#brands').html(brandTag);
	$('#productTypes').html(productTag);
}

function onSizeChange(size) {
	var name = document.getElementById("productNames").value;
	var colourTag = '<option selected disabled="disabled" value="">Colour</option>';
	var brandTag = '<option selected disabled="disabled" value="">Brand</option>';
	var productTag = '<option selected disabled="disabled" value="">Product Type</option>';
	if (size) {
		$.ajax({
			url : '/loadColoursByProductNameAndSize',
			data : { "name" : name, "size" : size },
			success : function(result) {
				var result = JSON.parse(result);
				for (var i = 0; i < result.length; i++) {
					colourTag += '<option value="' + result[i] + '">'+ result[i]+ '</option>';
				}
				$('#colours').html(colourTag);
			}
		});

	}
	//reset data
	$('#colours').html(colourTag);
	$('#brands').html(brandTag);
	$('#productTypes').html(productTag);
}

function onColourChange(colour) {
	var name = document.getElementById("productNames").value;
	var size = document.getElementById("sizes").value;
	var brandTag = '<option selected disabled="disabled" value="">Brand</option>';
	var productTag = '<option selected disabled="disabled" value="">Product Type</option>';
	if (colour) {
		$.ajax({
			url : '/loadBrandsByProductNameAndSizeAndColour',
			data : { "name" : name, "size" : size, "colour" : colour },
			success : function(result) {
				var result = JSON.parse(result);
				for (var i = 0; i < result.length; i++) {
					brandTag += '<option value="' + result[i] + '">'+ result[i]+ '</option>';
				}
				$('#brands').html(brandTag);
			}
		});

	}
	//reset data
	$('#brands').html(brandTag);
	$('#productTypes').html(productTag);
}

function onBrandChange(brand) {
	var name = document.getElementById("productNames").value;
	var size = document.getElementById("sizes").value;
	var colour = document.getElementById("colours").value;
	var productTag = '<option selected disabled="disabled" value="">Product Type</option>';
	if (brand) {
		$.ajax({
			url : '/loadProductTypesByProductNameAndSizeAndColourAndBrand',
			data : { "name" : name, "size" : size, "colour" : colour, "brand" : brand },
			success : function(result) {
				var result = JSON.parse(result);
				for (var i = 0; i < result.length; i++) {
					productTag += '<option value="' + result[i][0] + '">'+ result[i][1]+ '</option>';
				}
				$('#productTypes').html(productTag);
			}
		});

	}
	//reset data
	$('#productTypes').html(productTag);
}

function setMaxQuantity(productId) {
	
	var quantityTagPrefix = `<input type="number" class="form-control" id="quantity" placeholder="Quantity`;
	var quantityTagSuffix = `" onChange="calculateAmount()">`;
	var maxQuantityTag = `<input type="hidden" id="maxQuantity" value=`;

	if (productId) {
		$.ajax({
			url : '/loadMaxQuantityForProduct',
			data : { "productId" : productId },
			success : function(result) {
				$('#quantityOuter').html(quantityTagPrefix + `(`+ result + ` available)` + quantityTagSuffix + maxQuantityTag + result + `>`);
			}
		});
	}
	//reset data
	$('#quantityOuter').html(quantityTagPrefix + quantityTagSuffix + maxQuantityTag + `"0">`);
}

