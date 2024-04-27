**free

/if defined(*CRTBNDRPG)
ctl-opt dftactgrp(*no) actgrp(*caller);
/endif
ctl-opt option(*srcstmt) bnddir('MDRFRAME');

dcl-f ROBOPATHD workstn sfile(LOCS: LOCSRRN)
                        sfile(LOG:  LOGRRN)
                        INDDS(FuncKey)
                        usropn;

/copy MDRFRAME

dcl-pr QCMDEXC extpgm('QCMDEXC');
  command char(2000) const;
  length  packed(15: 5) const;
  igc char(3) const options(*nopass);
end-pr;

dcl-pr QRCVDTAQ extpgm('QSYS/QRCVDTAQ');
  dataQueue    char(10)      const;
  dataQueueLib char(10)      const;
  dataLength   packed(5: 0);
  data         char(32767)   options(*varsize);
  wait         packed(5: 0)  const;
end-pr;

dcl-ds FuncKey;
  F3 ind pos(3);
  F5 ind pos(5);
  clearLocs ind pos(50);
  showLocs  ind pos(51);
  clearLog  ind pos(60);
  showLog   ind pos(61);
end-ds;

dcl-s LOCSRRN packed(4: 0);
dcl-s LOGRRN  packed(4: 0);
dcl-s HIGHLOC packed(4: 0);
dcl-s HIGHLOG packed(4: 0);

exec SQL set option commit=*none, naming=*sys;


callp(e) QCMDEXC('DLTDTAQ DTAQ(ROBOLOGDQ)': 2000);

QCMDEXC('CRTDTAQ DTAQ(ROBOLOGDQ) +
                 TYPE(*STD) +
                 MAXLEN(80) +
                 FORCE(*NO) +
                 TEXT(''Robot log auto refresh'') +
                 SEQ(*FIFO)': 2000);

QCMDEXC('OVRDSPF FILE(ROBOPATHD) DTAQ(ROBOLOGDQ)': 2000);

open ROBOPATHD;

LoadLog();
LoadLocs();
showScreen();

close ROBOPATHD;
callp(e) QCMDEXC('DLTDTAQ DTAQ(ROBOLOGDQ)': 2000);


*inlr = *on;

dcl-proc loadLog;

  dcl-s histts timestamp;
  dcl-s histname char(30);

  clearLog = *on;
  write LOGCTL;
  clearLog = *off;
  LOGRRN = 0;
  HIGHLOG  = 0;
  showLog = *off;

  exec SQL
    declare C2 cursor for
    select histts, histname
      from HISTLOG;

  exec SQL
    open C2;

  dow %subst(sqlstt:1:2) = '00' or %subst(sqlstt:1:2) = '01';
    exec SQL fetch next from C2 into :histts, :histname;
    if %subst(sqlstt:1:2) = '00' or %subst(sqlstt:1:2) = '01';
      logrrn += 1;
      highlog += 1;
      showLog = *on;
      logtime = %char(histts);
      logloc  = histname;
      write LOG;
    endif;
  enddo;

  exec SQL close C2;

end-proc;

dcl-proc LoadLocs;

  clearLocs = *on;
  write LOCSCTL;
  clearLocs = *off;
  LOCSRRN = 0;
  HIGHLOC = 0;
  showLocs = *off;

  exec SQL
    declare C1 cursor for
    select name, xcoor, ycoor
      from LOCMAST
      order by name;

  exec SQL
    open C1;

  dow %subst(sqlstt:1:2) = '00' or %subst(sqlstt:1:2) = '01';
    exec SQL fetch next from C1 into :name, :x, :y;
    if %subst(sqlstt:1:2) = '00' or %subst(sqlstt:1:2) = '01';
      ord = ' ';
      locsrrn += 1;
      highloc += 1;
      showLocs = *on;
      write LOCS;
    endif;
  enddo;

  exec SQL close C1;

end-proc;

dcl-proc showScreen;

  dcl-s dqrec char(80);
  dcl-s dqlen packed(5: 0);

  dou F3 = *on;
    write logctl;
    write locsctl;

    QRCVDTAQ('ROBOLOGDQ': '*LIBL': dqlen: dqrec: -1);
    if %len(dqrec) < 5;
      leave;
    endif;

    if %subst(dqrec:1:5) = '*DSPF';

      read(e) locsctl;

      if F3 = *on;
        leave;
      endif;

      writeLastPath();

    elseif %subst(dqrec: 1: 8) = '*REFRESH';
      loadLog();
    endif;

  enddo;

end-proc;

dcl-proc writeLastPath;

  dcl-ds path dim(10) qualified;
    name char(30);
    x    packed(7: 3);
    y    packed(7: 3);
  end-ds;

  dcl-ds seqpath dim(10) qualified;
    ord  char(1);
    name char(30);
    x    packed(7: 3);
    y    packed(7: 3);
  end-ds;

  dcl-s pathLength int(10);
  dcl-s handle like(MDR_Handle_t) based(p_handle);
  dcl-s i int(10);

  for LOCSRRN = 1 to HighLoc;
    chain LOCSRRN LOCS;
    if ord >= '1' and ord <= '9';
      pathLength += 1;
      seqpath(pathLength).ord  = ord;
      seqpath(pathLength).name = name;
      seqpath(pathLength).x    = x;
      seqpath(pathLength).y    = y;
    endif;
  endfor;

  if pathLength > 0;

    sorta %subarr(seqpath:1:pathLength) %fields(ord: name);

    for i = 1 to pathLength;
      path(i).name = seqpath(i).name;
      path(i).x    = seqpath(i).x;
      path(i).y    = seqpath(i).y;
    endfor;

    p_handle = MDR_newHandle();
    MDR_genParseOptions(handle:'document_name=path');

    data-gen %subarr(path:1:pathLength)
            %data('/tmp/lastRobotPath.json': 'doc=file')
            %gen('MDRFRAME(GENERATOR)');

    MDR_freeHandle(p_handle);

  endif;

  for LOCSRRN = 1 to HIGHLOC;
    chain LOCSRRN LOCS;
    if %found;
      ord = ' ';
      update LOCS;
    endif;
  endfor;

  if pathLength > 0;
    msg = '     Path changed successfully!     ';
    exfmt(e) msgbox;
  endif;

end-proc;

