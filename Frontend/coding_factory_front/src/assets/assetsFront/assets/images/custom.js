$('.project-image').each(function() {
    const imgPath = $(this).data('image');
    $(this).css('background-image', `url(assets/assetsFront/assets/images/${imgPath})`);
}); 