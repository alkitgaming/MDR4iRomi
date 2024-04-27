**FREE
// Below is the default compilation command for this generated source
// CRTBNDRPG PGM(SKMDR/ROMILOGR) SRCFILE(SKMDR/QRPGLESRC) -
// SRCMBR(ROMILOGR) OPTION(*EVENTF) DBGVIEW(*ALL) -
// BNDDIR(MDRFRAME) USRPRF(*OWNER) TGTRLS(*CURRENT)

/if defined(*CRTBNDRPG)
ctl-opt dftactgrp(*no) actgrp(*caller);
/endif
ctl-opt option(*srcstmt) bnddir('MDRFRAME');

/copy MDRFRAME

dcl-pi *n;
  handle    like(MDR_Handle_t);
  method    char(32) const;
  body      varchar(500000); // MAxSize: 16000000
end-pi;

dcl-pr QSNDDTAQ extpgm('QSYS/QSNDDTAQ');
  DataQueue    char(10)     const;
  DataQueueLib char(10)     const;
  DataLength   packed(5: 0) const;
  Data         char(80)     const;
end-pr;

exec SQL set option commit=*none, naming=*sys;

select;
  when method = 'POST';
    mdr_post();
endsl;
*inlr = *on;
return;

//----------------------------------------------------------------------------
// Process post Method
//----------------------------------------------------------------------------
dcl-proc mdr_post;


  // Request payload definitions
  dcl-ds location qualified;
    location varchar(30)     inz;
  end-ds;


  // Response payload definitions
  dcl-ds locwithts qualified;
    location  varchar(30)     inz;
    timestamp timestamp       inz;
  end-ds;


  // Response payload definitions
  dcl-ds error qualified;
    code    packed(10: 0)   inz;
    message varchar(100)    inz;
  end-ds;

  // Define common use variables
  dcl-s errorMsg varchar(100:4);
  dcl-s status   zoned(3) inz(200);

  // Variable used by DATA-GEN to hold generated JSON. Initialize it
  // with IFS path if using "doc=file" option in DATA-GEN. Otherwise,
  // must be large enough to contain full payload.
  dcl-s result varchar(500);

  dcl-s ts timestamp;
  dcl-s loc char(30);


  // Logic to parse request body
  MDR_genParseOptions(handle: 'document_name=location');

  data-into location %data('': 'case=convert +
                                 countprefix=num_ +
                                 allowmissing=yes +
                                 allowextra=yes')
                     %parser('MDRFRAME(PARSER)':handle);

  locwithts.location = location.location;
  locwithts.timestamp = %timestamp();
  ts = locwithts.timestamp;
  loc = locwithts.location;

  exec SQL insert into HISTLOG
     values( :ts, :loc );

  if %subst(sqlstt:1:2) <> '00' and %subst(sqlstt:1:2) <> '01';
     error.code = 2000;
     error.message = 'SQL state ' + sqlstt + ' writing to log';
     status = 500;
  endif;


  // Insert logic to set HTTP status code. Example:
  // status = 400 // for inbound request errors
  // status = 500 // for api execution errors

  // Logic to generate response
  select;
  when status = 200;
    MDR_genParseOptions(handle: 'document_name=locwithts convts=yes');
    data-gen locwithts %data(result: 'doc=string +
                                      countprefix=num_ +
                                      renameprefix=name_')
                       %gen('MDRFRAME(GENERATOR)':handle);

    monitor;
      QSNDDTAQ( 'ROBOLOGDQ'
              : '*LIBL'
              : 80
              : '*REFRESH' );
    on-error;
    endmon;

  when status = 400;
    MDR_genParseOptions(handle: 'document_name=error');
    data-gen error %data(result:  'doc=string +
                                   countprefix=num_ +
                                   renameprefix=name_')
                       %gen('MDRFRAME(GENERATOR)':handle);
  when status = 500;
    MDR_genParseOptions(handle: 'document_name=error');
    data-gen error %data(result:  'doc=string +
                                   countprefix=num_ +
                                   renameprefix=name_')
                       %gen('MDRFRAME(GENERATOR)':handle);
  endsl;
  MDR_setStatus(handle:status);
end-proc;

