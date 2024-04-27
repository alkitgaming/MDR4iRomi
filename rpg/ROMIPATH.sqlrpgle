**FREE
// Below is the default compilation command for this generated source
// CRTBNDRPG PGM(SKMDR/ROMIPATH) SRCFILE(SKMDR/QRPGLESRC) -
// SRCMBR(ROMIPATH) OPTION(*EVENTF) DBGVIEW(*ALL) -
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

exec SQL set option commit=*none, naming=*sys;

select;
  when method = 'GET';
    mdr_get();
endsl;
*inlr = *on;
return;

//----------------------------------------------------------------------------
// Process get Method
//----------------------------------------------------------------------------
dcl-proc mdr_get;


  // Response payload definitions
  dcl-ds robotpath dim(10) qualified;
    name          varchar(30)     inz;
    x             packed(7: 3)    inz;
    y             packed(7: 3)    inz;
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
  dcl-s pathLength int(10);

  if MDR_readBodyFromFile( handle
                         : MDR_BODY_RESPONSE
                         : '/tmp/lastRobotPath.json'
                         : errorMsg ) = -1;
     error.code = 1000;
     error.message = errorMsg;
     status = 500;
  endif;


  // Insert logic to set HTTP status code. Example:
  // status = 400 // for inbound request errors
  // status = 500 // for api execution errors

  // Logic to generate response
  select;
  when status = 200;
    // MDR_genParseOptions(handle: 'document_name=robotpath');
    // data-gen %subarr(robotpath:1:pathLength) %data(result: 'doc=string +
    //                                countprefix=num_ +
    //                                renameprefix=name_')
    //                    %gen('MDRFRAME(GENERATOR)':handle);
  when status = 400;
    MDR_genParseOptions(handle: 'document_name=error');
    data-gen error %data(result: 'doc=string +
                                   countprefix=num_ +
                                   renameprefix=name_')
                       %gen('MDRFRAME(GENERATOR)':handle);
  when status = 500;
    MDR_genParseOptions(handle: 'document_name=error');
    data-gen error %data(result: 'doc=string +
                                   countprefix=num_ +
                                   renameprefix=name_')
                       %gen('MDRFRAME(GENERATOR)':handle);
  endsl;
  MDR_setStatus(handle:status);
end-proc;
