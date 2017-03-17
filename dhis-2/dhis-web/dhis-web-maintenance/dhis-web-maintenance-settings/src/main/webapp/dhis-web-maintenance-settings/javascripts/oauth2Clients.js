var OAuth2Service = {
  save: function(o, id) {
    if( !!id ) {
      return $.ajax({
        url: '../api/oAuth2Clients/' + id,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(o)
      });
    } else {
      return $.ajax({
        url: '../api/oAuth2Clients',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(o)
      });
    }
  },
  load: function(id) {
    return $.ajax({
      url: '../api/oAuth2Clients/' + id
    })
  },
  fromJson: function(o) {
    $('#name').val(o.name);
    $('#clientId').val(o.cid);
    $('#clientSecret').val(o.secret);

    if( o.grantTypes.indexOf('password') != -1 ) {
      $('#gtPassword').attr('checked', true);
    }

    if( o.grantTypes.indexOf('refresh_token') != -1 ) {
      $('#gtRefreshToken').attr('checked', true);
    }

    if( o.grantTypes.indexOf('authorization_code') != -1 ) {
      $('#gtAuthorizationCode').attr('checked', true);
    }

    o.redirectUris.forEach(function(el) {
      $('<option/>').attr('value', el).text(el).appendTo('#redirectUris');
    });
  },
  toJson: function() {
    var o = {};
    o.grantTypes = [];
    o.redirectUris = [];

    o.name = $('#name').val();
    o.cid = $('#clientId').val();
    o.secret = $('#clientSecret').val();

    if( $('#gtPassword').is(':checked') ) {
      o.grantTypes.push("password");
    }

    if( $('#gtRefreshToken').is(':checked') ) {
      o.grantTypes.push("refresh_token");
    }

    if( $('#gtAuthorizationCode').is(':checked') ) {
      o.grantTypes.push("authorization_code");
    }

    $("#redirectUris").children().each(function(idx, el) {
      o.redirectUris.push($(el).val());
    });

    return o;
  },
  getUuid: function() {
    var def = $.Deferred();

    $.ajax({
      url: '../api/system/uuid',
      dataType: 'json'
    }).done(function(o) {
      def.resolve(o.codes[0]);
    });

    return def.promise();
  }
};

function updateO2Client(context) {
  location.href = 'getOAuth2Client.action?id=' + context.uid;
}

function deleteO2Client(context) {
  if( window.confirm(i18n_confirm_delete) ) {
    $.ajax({
      url: '../api/oAuth2Clients/' + context.uid,
      type: 'DELETE'
    }).done(function() {
      location.reload();
    });
  }
}
